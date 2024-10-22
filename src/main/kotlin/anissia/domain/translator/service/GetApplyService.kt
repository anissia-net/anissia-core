package anissia.domain.translator.service

import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.translator.core.model.GetApplyCommand
import anissia.domain.translator.core.model.TranslatorApplyItem
import anissia.domain.translator.infrastructure.ApplyValue
import org.springframework.stereotype.Service

@Service
class GetApplyService(
    private val agendaRepository: AgendaRepository,
): GetApply {
    override fun handle(cmd: GetApplyCommand): TranslatorApplyItem {
        cmd.validate()
        return agendaRepository.findWithPollsByAgendaNoAndCode(cmd.applyNo, ApplyValue.CODE)
            ?.let { TranslatorApplyItem(it, true) }
            ?: TranslatorApplyItem()
    }
}
