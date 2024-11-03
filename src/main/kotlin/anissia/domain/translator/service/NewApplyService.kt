package anissia.domain.translator.service

import anissia.domain.agenda.Agenda
import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.session.model.SessionItem
import anissia.domain.translator.infrastructure.ApplyValue
import anissia.domain.translator.command.NewApplyCommand
import anissia.shared.ResultWrapper
import gs.shared.FailException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class NewApplyService(
    private val agendaRepository: AgendaRepository,
    private val isApplying: IsApplying,
): NewApply {
    @Transactional
    override fun handle(cmd: NewApplyCommand, sessionItem: SessionItem): ResultWrapper<Long> {
        cmd.validate()
        sessionItem.validateLogin()

        if (sessionItem.isAdmin) {
            throw FailException("이미 권한이 있습니다.")
        }

        if (isApplying.handle(sessionItem)) {
            throw FailException("신청중인 진행사항이 있습니다.")
        }

        if (agendaRepository.existsByCodeAndStatusAndAnAndUpdDtAfter(ApplyValue.CODE, "DONE", sessionItem.an, OffsetDateTime.now().minusDays(7))) {
            throw FailException("심사완료 일주일 후부터 재심사를 요청할 수 있습니다.")
        }

        agendaRepository.saveAndFlush(
            Agenda(
                code = ApplyValue.CODE,
                status = "ACT",
                an = sessionItem.an,
                data1 = "ACT",
                data2 = sessionItem.name,
                data3 = cmd.website,
            )
        ).run { return ResultWrapper.ok(agendaNo) }
    }
}
