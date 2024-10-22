package anissia.domain.account.service

import anissia.domain.account.model.EditUserNameCommand
import anissia.domain.account.repository.AccountBanNameRepository
import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.core.model.NewActivePanelTextCommand
import anissia.domain.activePanel.core.ports.inbound.NewActivePanelText
import anissia.domain.agenda.core.Agenda
import anissia.domain.agenda.core.ports.outbound.AgendaRepository
import anissia.domain.anime.core.model.UpdateAnimeDocumentCommand
import anissia.domain.anime.core.ports.inbound.UpdateAnimeDocument
import anissia.domain.anime.core.ports.outbound.AnimeCaptionRepository
import anissia.domain.session.core.model.Session
import anissia.domain.translator.core.ports.inbound.IsApplying
import anissia.infrastructure.service.BCryptService
import anissia.shared.ResultWrapper
import gs.shared.FailException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class EditUserNameService(
    private val bCryptService: BCryptService,
    private val accountRepository: AccountRepository,
    private val accountBanNameRepository: AccountBanNameRepository,
    private val isApplying: IsApplying,
    private val agendaRepository: AgendaRepository,
    private val newActivePanelText: NewActivePanelText,
    private val updateAnimeDocument: UpdateAnimeDocument,
    private val animeCaptionRepository: AnimeCaptionRepository,
): EditUserName {

    val codeUpdateName = "AC-UPD-NAME"

    @Transactional
    override fun handle(cmd: EditUserNameCommand, session: Session): ResultWrapper<Unit> {
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
            newActivePanelText.handle(NewActivePanelTextCommand("운영진 [$oldName]님의 닉네임이 [$newName]님으로 변경되었습니다."), null)
        }

        return ResultWrapper.ok()
    }

    private fun getUserTakeIfPassword(password: String, session: Session) =
        accountRepository.findByIdOrNull(session.an)
            ?.takeIf { bCryptService.matches(password, it.password) }
}
