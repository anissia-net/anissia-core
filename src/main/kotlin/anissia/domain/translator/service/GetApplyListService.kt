package anissia.domain.translator.service

import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.translator.infrastructure.ApplyValue
import anissia.domain.translator.command.GetApplyListCommand
import anissia.domain.translator.model.TranslatorApplyItem
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
