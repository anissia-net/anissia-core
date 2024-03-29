package anissia.domain.translator.core.service

import anissia.domain.agenda.core.ports.outbound.AgendaRepository
import anissia.domain.translator.core.model.GetApplyListCommand
import anissia.domain.translator.core.model.TranslatorApplyItem
import anissia.domain.translator.core.ports.inbound.GetApplyList
import anissia.domain.translator.infrastructure.ApplyValue
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class GetApplyListService(
    private val agendaRepository: AgendaRepository,
): GetApplyList {
    override fun handle(cmd: GetApplyListCommand): Page<TranslatorApplyItem> {
        cmd.validate()
        return agendaRepository.findAllByCodeOrderByStatusAscAgendaNoDesc(ApplyValue.CODE, PageRequest.of(cmd.page, 30)).map { TranslatorApplyItem(it) }
    }
}
