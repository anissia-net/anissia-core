package anissia.rdb.domain

import anissia.rdb.repository.AgendaPollsRepository
import anissia.rdb.repository.AgendaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class TranslatorService(
    private val agendaRepository: AgendaRepository,
    private val agendaPollsRepository: AgendaPollsRepository
) {
    private val code = "TRANSLATOR-APPLY"

    fun getApplyList(page: Int) = agendaRepository.findAllByCodeOrderByStatusDescAndAgendaNoDesc(code, PageRequest.of(page, 30))

    fun getApply(applyNo: Long) {

    }

    fun createApply() {

    }

    fun deleteApply() {

    }

    fun createApplyPoll() {

    }

    fun deleteApplyPoll() {

    }
}