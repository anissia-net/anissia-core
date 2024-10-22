package anissia.domain.translator.core.service

import anissia.domain.account.core.model.AccountRole
import anissia.domain.account.core.repository.AccountRepository
import anissia.domain.activePanel.core.model.NewActivePanelTextCommand
import anissia.domain.activePanel.core.ports.inbound.NewActivePanelText
import anissia.domain.agenda.core.Agenda
import anissia.domain.agenda.core.AgendaPoll
import anissia.domain.agenda.core.ports.outbound.AgendaPollRepository
import anissia.domain.agenda.core.ports.outbound.AgendaRepository
import anissia.domain.session.core.model.Session
import anissia.domain.translator.core.model.NewApplyPollCommand
import anissia.domain.translator.core.ports.inbound.NewApplyPoll
import anissia.domain.translator.infrastructure.ApplyValue
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class NewApplyPollService(
    private val agendaRepository: AgendaRepository,
    private val agendaPollRepository: AgendaPollRepository,
    private val accountRepository: AccountRepository,
    private val newActivePanelText: NewActivePanelText,
): NewApplyPoll {
    @Transactional
    override fun handle(cmd: NewApplyPollCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateAdmin()

        var point = cmd.point.toInt()

        val app = agendaRepository.findByIdOrNull(cmd.applyNo)?.takeIf { it.code == ApplyValue.CODE }
            ?: return ResultWrapper.fail("존재하지 않는 신청입니다.")

        app.takeIf { it.status == "ACT" }
            ?: return ResultWrapper.fail("종료된 신청서입니다.")

        val polls = app.polls
        if (point != 0) {
            if (polls.filter { it.an == session.an }.any { it.vote != 0 }) {
                return ResultWrapper.fail("찬성/반대는 한 신청처에 한번만 할 수 있습니다.")
            }
        }

        if (session.isRoot) {
            point *= 10
        }
        val poll = agendaPollRepository.save(
            AgendaPoll(
                agenda = app,
                voteUp = if (point > 0) point else 0,
                voteDown = if (point < 0) point else 0,
                name = session.name,
                an = session.an,
                comment = cmd.comment
            )
        )

        val vote = (polls + poll).sumOf { it.vote }
        if (vote >= 3) {
            app.status = "DONE"
            app.data1 = "PASS"
            val account = accountRepository.findByIdOrNull(app.an)!!
            account.roles.add(AccountRole.TRANSLATOR)
            accountRepository.save(account)
            newActivePanelText.handle(NewActivePanelTextCommand("[${account.name}]님이 자막제작자로 참여하였습니다."), null)
            agendaPollRepository.save(toApplySystemPoll(app, "조건이 충족되어 권한이 부여되었습니다."))
        } else if (vote <= -3) {
            app.status = "DONE"
            app.data1 = "FAIL"
            agendaPollRepository.save(toApplySystemPoll(app, "최종 반려되었습니다. (7일 후 재신청이 가능합니다.)"))
        }
        agendaRepository.save(app.apply { updDt = OffsetDateTime.now() })

        return ResultWrapper.ok()
    }

    private fun toApplySystemPoll(agenda: Agenda, comment: String) = AgendaPoll(
            agenda = agenda,
            name = "",
            an = 0,
            comment = comment,
    )
}
