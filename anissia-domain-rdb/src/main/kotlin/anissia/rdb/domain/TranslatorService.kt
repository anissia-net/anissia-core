package anissia.rdb.domain

import anissia.rdb.repository.AgendaPollsRepository
import anissia.rdb.repository.AgendaRepository
import org.springframework.stereotype.Service

@Service
class TranslatorService(
    private val agendaRepository: AgendaRepository,
    private val agendaPollsRepository: AgendaPollsRepository
) {
    fun getApplyList() {

    }

    fun getApply() {

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