package anissia.domain.account.service

import anissia.domain.account.model.AccountUserItem
import anissia.domain.account.model.EditUserNameCommand
import anissia.domain.account.model.EditUserPasswordCommand
import anissia.domain.account.repository.AccountBanNameRepository
import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.model.AddTextActivePanelCommand
import anissia.domain.activePanel.service.ActivePanelService
import anissia.domain.agenda.Agenda
import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.anime.model.UpdateAnimeDocumentCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.service.UpdateAnimeDocument
import anissia.domain.session.model.Session
import anissia.domain.translator.service.IsApplying
import anissia.infrastructure.service.BCryptService
import anissia.shared.ResultWrapper
import gs.shared.FailException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class UserServiceImpl(
    private val bCryptService: BCryptService,
    private val accountRepository: AccountRepository,
    private val accountBanNameRepository: AccountBanNameRepository,
    private val isApplying: IsApplying,
    private val agendaRepository: AgendaRepository,
    private val activePanelService: ActivePanelService,
    private val updateAnimeDocument: UpdateAnimeDocument,
    private val animeCaptionRepository: AnimeCaptionRepository,
): UserService {

    val codeUpdateName = "AC-UPD-NAME"

    override fun get(session: Session): AccountUserItem =
        AccountUserItem.cast(accountRepository.findWithRolesByAn(session.an)!!)

    override fun editPassword(cmd: EditUserPasswordCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateLogin()

        val account = accountRepository.findByIdOrNull(session.an)
            ?.takeIf { bCryptService.matches(it.password, cmd.oldPassword) }
            ?: return ResultWrapper.fail("기존 암호가 일치하지 않습니다.")

        account.password = bCryptService.encode(cmd.newPassword)
        accountRepository.save(account)
        return ResultWrapper.ok()
    }

    @Transactional
    override fun editName(cmd: EditUserNameCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateLogin()

        val account = accountRepository.findByIdOrNull(session.an)
            ?.takeIf { bCryptService.matches(it.password, cmd.password) }
            ?: return ResultWrapper.fail("암호가 일치하지 않습니다.")

        val oldName = account.name
        val newName = cmd.name

        if (oldName == newName) {
            return ResultWrapper.fail("기존 이름과 같습니다.")
        }

        if (isApplying.handle(session)) {
            throw FailException("자막제작자 신청중에는 이름을 바꿀 수 없습니다.")
        }

        if (agendaRepository.existsByCodeAndStatusAndAnAndUpdDtAfter(codeUpdateName, "DONE", session.an, OffsetDateTime.now().minusDays(1))) {
            return ResultWrapper.fail("이름은 하루에 한번만 바꿀 수 있습니다.")
        }

        if (accountRepository.existsByName(newName) || accountBanNameRepository.existsById(newName)) {
            return ResultWrapper.fail("사용중이거나 사용할 수 없는 이름입니다.")
        }

        agendaRepository.saveAndFlush(
            Agenda(
                code = codeUpdateName,
                status = "DONE",
                an = session.an,
                data1 = "DONE",
                data2 = oldName,
                data3 = newName,
            )
        )

        accountRepository.saveAndFlush(account.apply { name = newName })

        // 운영진
        if (account.roles.isNotEmpty()) {
            animeCaptionRepository.findAllByAn(session.an).mapNotNull { it.anime?.animeNo }.forEach{
                updateAnimeDocument.handle(UpdateAnimeDocumentCommand(it))
            }
            activePanelService.addText(AddTextActivePanelCommand("운영진 [$oldName]님의 닉네임이 [$newName]님으로 변경되었습니다."), null)
        }

        return ResultWrapper.ok()
    }

    private fun getUserTakeIfPassword(password: String, session: Session) =
        accountRepository.findByIdOrNull(session.an)
            ?.takeIf { bCryptService.matches(password, it.password) }
}
