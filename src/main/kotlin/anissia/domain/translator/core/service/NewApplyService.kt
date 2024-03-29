package anissia.domain.translator.core.service

import anissia.domain.agenda.core.Agenda
import anissia.domain.agenda.core.ports.outbound.AgendaRepository
import anissia.domain.session.core.model.Session
import anissia.domain.translator.core.model.NewApplyCommand
import anissia.domain.translator.core.ports.inbound.IsApplying
import anissia.domain.translator.core.ports.inbound.NewApply
import anissia.domain.translator.infrastructure.ApplyValue
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
    override fun handle(cmd: NewApplyCommand, session: Session): ResultWrapper<Long> {
        cmd.validate()
        session.validateLogin()

        if (session.isAdmin) {
            throw FailException("이미 권한이 있습니다.")
        }

        if (isApplying.handle(session)) {
            throw FailException("신청중인 진행사항이 있습니다.")
        }

        if (agendaRepository.existsByCodeAndStatusAndAnAndUpdDtAfter(ApplyValue.CODE, "DONE", session.an, OffsetDateTime.now().minusDays(7))) {
            throw FailException("심사완료 일주일 후부터 재심사를 요청할 수 있습니다.")
        }

        agendaRepository.saveAndFlush(
            Agenda(
                code = ApplyValue.CODE,
                status = "ACT",
                an = session.an,
                data1 = "ACT",
                data2 = session.name,
                data3 = cmd.website,
            )
        ).run { return ResultWrapper.ok(agendaNo) }
    }
}
