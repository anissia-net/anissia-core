package anissia.domain.account.service

import anissia.domain.account.command.EditUserNameCommand
import anissia.domain.account.command.EditUserPasswordCommand
import anissia.domain.account.model.AccountUserItem
import anissia.domain.account.repository.AccountBanNameRepository
import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.command.AddTextActivePanelCommand
import anissia.domain.activePanel.service.ActivePanelLogService
import anissia.domain.agenda.Agenda
import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.anime.command.UpdateAnimeDocumentCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.service.AnimeDocumentService
import anissia.domain.session.model.SessionItem
import anissia.domain.translator.service.TranslatorApplyService
import anissia.infrastructure.service.BCryptService
import anissia.shared.ApiResponse
import anissia.shared.ApiFailException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@Service
class UserServiceImpl(
    private val bCryptService: BCryptService,
    private val accountRepository: AccountRepository,
    private val accountBanNameRepository: AccountBanNameRepository,
    private val translatorApplyService: TranslatorApplyService,
    private val agendaRepository: AgendaRepository,
    private val activePanelLogService: ActivePanelLogService,
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
            .filter { bCryptService.matches(it.password, cmd.oldPassword) }
            .switchIfEmpty(Mono.error(ApiFailException("기존 암호가 일치하지 않습니다.")))
            .flatMap { accountRepository.save(it.apply { password = bCryptService.encode(cmd.newPassword) }) }.then()

    @Transactional
    override fun editName(cmd: EditUserNameCommand, sessionItem: SessionItem): Mono<Void> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .doOnNext { sessionItem.validateLogin() }
            .flatMap { accountRepository.findById(sessionItem.an) }
            .filter { bCryptService.matches(it.password, cmd.password) }
            .switchIfEmpty(Mono.error(ApiFailException("암호가 일치하지 않습니다.")))
            .filter { it.name != cmd.name }
            .switchIfEmpty(Mono.error(ApiFailException("기존 이름과 같습니다.")))
            .filterWhen { translatorApplyService.isApplying(sessionItem).map { !it } }
            .switchIfEmpty(Mono.error(ApiFailException("자막제작자 신청중에는 이름을 바꿀 수 없습니다.")))
            .filterWhen { agendaRepository.existsByCodeAndStatusAndAnAndUpdDtAfter(codeUpdateName, "DONE", sessionItem.an, OffsetDateTime.now().minusDays(1)).map { !it } }
            .switchIfEmpty(Mono.error(ApiFailException("이름은 하루에 한번만 바꿀 수 있습니다.")))
            .filterWhen { accountRepository.existsByName(cmd.name).map { !it } }
            .switchIfEmpty(Mono.error(ApiFailException("사용중이거나 사용할 수 없는 이름입니다.")))
            .flatMap { agendaRepository.save(Agenda.changeName(sessionItem.an, it.name, cmd.name)).thenReturn(it) }
            .flatMap { accountRepository.save(it.apply { name = cmd.name }) }



        // 운영진
        if (account.roles.isNotEmpty()) {
            animeCaptionRepository.findAllByAn(sessionItem.an).mapNotNull { it.anime?.animeNo }.forEach{
                animeDocumentService.update(UpdateAnimeDocumentCommand(it))
            }
            activePanelLogService.addText(AddTextActivePanelCommand("운영진 [$oldName]님의 닉네임이 [$newName]님으로 변경되었습니다."), null)
        }

        return ApiResponse.ok()
    }

    private fun getUserTakeIfPassword(password: String, sessionItem: SessionItem) =
        accountRepository.findByIdOrNull(sessionItem.an)
            ?.takeIf { bCryptService.matches(password, it.password) }
}
