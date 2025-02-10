package anissia.domain.account.service

import anissia.domain.account.Account
import anissia.domain.account.command.EditUserNameCommand
import anissia.domain.account.command.EditUserPasswordCommand
import anissia.domain.account.model.AccountUserItem
import anissia.domain.account.repository.AccountBanNameRepository
import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.service.ActivePanelService
import anissia.domain.agenda.Agenda
import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.anime.command.UpdateAnimeDocumentCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.service.AnimeDocumentService
import anissia.domain.session.model.SessionItem
import anissia.domain.translator.service.TranslatorApplyService
import anissia.infrastructure.common.enBCrypt
import anissia.infrastructure.common.eqBCrypt
import anissia.infrastructure.common.subscribeBoundedElastic
import anissia.shared.ApiFailException
import org.springframework.data.repository.findByIdOrNull
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
        Mono.justOrEmpty<Account>(accountRepository.findWithRolesByAn(sessionItem.an))
            .map { AccountUserItem.create(it) }
            .switchIfEmpty(Mono.error(ApiFailException("사용자 정보를 찾을 수 없습니다.")))



    override fun editPassword(cmd: EditUserPasswordCommand, sessionItem: SessionItem): Mono<String> =
        Mono.defer<Account> {
            cmd.validate()
            sessionItem.validateLogin()
            Mono.justOrEmpty(accountRepository.findByIdOrNull(sessionItem.an))
        }
            .filter { account -> account.password.eqBCrypt(cmd.oldPassword) }
            .switchIfEmpty(Mono.error(ApiFailException("기존 암호가 일치하지 않습니다.")))
            .map { account -> account.apply { this.password = cmd.newPassword.enBCrypt } }
            .map { accountRepository.save(it); "" }


    @Transactional
    override fun editName(cmd: EditUserNameCommand, sessionItem: SessionItem): Mono<String> =
        Mono.defer<Account> {
            cmd.validate()
            sessionItem.validateLogin()
            Mono.justOrEmpty(accountRepository.findByIdOrNull(sessionItem.an))
        }
            .filter { account -> account.password.eqBCrypt(cmd.password) }
            .switchIfEmpty(Mono.error(ApiFailException("암호가 일치하지 않습니다.")))
            .flatMap { account ->
                val oldName = account.name
                val newName = cmd.name

                if (oldName == newName) {
                    return@flatMap Mono.error(ApiFailException("기존 이름과 같습니다."))
                }

                if (translatorApplyService.isApplying(sessionItem).blockOptional().orElse(false)) {
                    return@flatMap Mono.error(ApiFailException("자막제작자 신청중에는 이름을 바꿀 수 없습니다."))
                }

                if (agendaRepository.existsByCodeAndStatusAndAnAndUpdDtAfter(codeUpdateName, "DONE", sessionItem.an, OffsetDateTime.now().minusDays(1))) {
                    return@flatMap Mono.error(ApiFailException("이름은 하루에 한번만 바꿀 수 있습니다."))
                }

                if (accountRepository.existsByName(newName) || accountBanNameRepository.existsById(newName)) {
                    return@flatMap Mono.error(ApiFailException("사용중이거나 사용할 수 없는 이름입니다."))
                }

                agendaRepository.save(
                    Agenda(
                        code = codeUpdateName,
                        status = "DONE",
                        an = sessionItem.an,
                        data1 = "DONE",
                        data2 = oldName,
                        data3 = newName,
                    )
                )

                accountRepository.save(account.apply { name = newName })

                // 운영진
                if (account.roles.isNotEmpty()) {
                    animeCaptionRepository.findAllByAn(sessionItem.an).mapNotNull { it.anime?.animeNo }.forEach{
                        animeDocumentService.update(UpdateAnimeDocumentCommand(it)).subscribeBoundedElastic()
                    }
                    activePanelService.addText(false, "운영진 [$oldName]님의 닉네임이 [$newName]님으로 변경되었습니다.", null).map { "" }
                } else {
                    Mono.just("")
                }
            }
}
