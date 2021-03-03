package anissia.services

import anissia.dto.ResultData
import anissia.dto.TranslatorApplyDto
import anissia.dto.request.TranslatorApplyRequest
import anissia.misc.As
import anissia.rdb.domain.Agenda
import anissia.rdb.repository.AgendaPollsRepository
import anissia.rdb.repository.AgendaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.text.DateFormat
import java.time.LocalDateTime
import javax.persistence.*

@Service
class TranslatorService(
    private val agendaRepository: AgendaRepository,
    private val agendaPollsRepository: AgendaPollsRepository,
    private val sessionService: SessionService
) {
    private val code = "TRANSLATOR-APPLY"
    private val user get() = sessionService.session

    fun getApplyList(page: Int) =
            agendaRepository.findAllByCodeOrderByStatusDescAgendaNoDesc(code, PageRequest.of(page, 30)).map { TranslatorApplyDto(it) }

    fun getApply(applyNo: Long) {

    }

    fun createApply(translatorApplyRequest: TranslatorApplyRequest): ResultData<Long> {
        translatorApplyRequest.validate()
//        if (user?.isManager() == true) {
//            return ResultData("FAIL", "이미 권한이 있습니다.")
//        }

        agendaRepository.saveAndFlush(Agenda(
                code = code,
                status = "ACT",
                an = user!!.an,
                data1 = user!!.name,
                data2 = translatorApplyRequest.website,
                data3 = LocalDateTime.now().format(As.DTF_ISO_YMD)
        )).run { return ResultData("OK", "", agendaNo) }
    }

    fun createApplyPoll() {

    }

    fun deleteApplyPoll() {

    }
}