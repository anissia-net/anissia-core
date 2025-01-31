package anissia.domain.account.service

import anissia.domain.account.command.EditUserNameCommand
import anissia.domain.account.command.EditUserPasswordCommand
import anissia.domain.account.model.AccountUserItem
import anissia.domain.account.repository.AccountBanNameRepository
import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.command.AddTextActivePanelCommand
import anissia.domain.activePanel.service.ActivePanelService
import anissia.domain.agenda.Agenda
import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.anime.Anime
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.service.AnimeDocumentService
import anissia.domain.session.model.SessionItem
import anissia.domain.translator.service.TranslatorApplyService
import anissia.infrastructure.common.doOnNextMono
import anissia.infrastructure.common.enBCrypt
import anissia.infrastructure.common.eqBCrypt
import anissia.shared.ApiFailException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@Service
class UserServiceImpl(
    private val accountRepository: AccountRepository,
    private val accountBanNameRepository: AccountBanNameRepository,
    private val translatorApplyService: TranslatorApplyService,
    private val agendaRepository: AgendaRepository,
    private val activePanelService: ActivePanelService,
    private val animeDocumentService: AnimeDocumentService,
    private val animeCaptionRepository: AnimeCaptionRepository,
): UserService {

    val codeUpdateName = "AC-UPD-NAME"

    override fun get(sessionItem: SessionItem): Mono<AccountUserItem> =
        accountRepository.findWithRolesByAn(sessionItem.an).map { AccountUserItem.create(it) }

    override fun editPassword(cmd: EditUserPasswordCommand, sessionItem: SessionItem): Mono<Void> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateLogin() }
            .flatMap { accountRepository.findById(sessionItem.an) }
            .filter { cmd.oldPassword.eqBCrypt(it.password) }
            .switchIfEmpty(Mono.error(ApiFailException("기존 암호가 일치하지 않습니다.")))
            .flatMap { accountRepository.save(it.apply { password = cmd.newPassword.enBCrypt }) }.then()

    @Transactional
    override fun editName(cmd: EditUserNameCommand, sessionItem: SessionItem): Mono<Void> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateLogin() }
            .flatMap { accountRepository.findById(sessionItem.an) }
            .filter { cmd.password.eqBCrypt(it.password) }
            .switchIfEmpty(Mono.error(ApiFailException("암호가 일치하지 않습니다.")))
            .filter { it.name != cmd.name }
            .switchIfEmpty(Mono.error(ApiFailException("기존 이름과 같습니다.")))
            .filterWhen { translatorApplyService.isApplying(sessionItem).map { !it } }
            .switchIfEmpty(Mono.error(ApiFailException("자막제작자 신청중에는 이름을 바꿀 수 없습니다.")))
            .filterWhen { agendaRepository.existsByCodeAndStatusAndAnAndUpdDtAfter(codeUpdateName, "DONE", sessionItem.an, OffsetDateTime.now().minusDays(1)).map { !it } }
            .switchIfEmpty(Mono.error(ApiFailException("이름은 하루에 한번만 바꿀 수 있습니다.")))
            .filterWhen { accountRepository.existsByName(cmd.name).map { !it } }
            .filterWhen { accountBanNameRepository.existsById(cmd.name).map { !it } }
            .switchIfEmpty(Mono.error(ApiFailException("사용중이거나 사용할 수 없는 이름입니다.")))
            .flatMap { agendaRepository.save(Agenda.changeName(sessionItem.an, it.name, cmd.name)).thenReturn(it) }
            .flatMap { val oldName = it.name; accountRepository.save(it.apply { name = cmd.name }).zipWith(Mono.just(oldName)) }
            .filter { it.t1.roles.isNotEmpty() }
            .doOnNextMono {
                animeCaptionRepository.findAllWithAnimeByAn(sessionItem.an)
                    .mapNotNull<Anime> { it.anime }
                    .flatMap { anime -> animeDocumentService.update(anime, false) }
                    .collectList()
            }
            .flatMap { activePanelService.addText(AddTextActivePanelCommand(false, "운영진 [${it.t2}]님의 닉네임이 [${it.t1.name}]님으로 변경되었습니다."), sessionItem) }
            .then()

}
