package anissia.services

import anissia.dto.ResultData
import anissia.dto.ResultStatus
import anissia.dto.TranslatorApplyDto
import anissia.dto.request.TranslatorApplyPollRequest
import anissia.dto.request.TranslatorApplyRequest
import anissia.rdb.entity.AccountRole
import anissia.rdb.entity.Agenda
import anissia.rdb.entity.AgendaPoll
import anissia.rdb.repository.AccountRepository
import anissia.rdb.repository.AgendaPollRepository
import anissia.rdb.repository.AgendaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TranslatorService(
        private val agendaRepository: AgendaRepository,
        private val agendaPollRepository: AgendaPollRepository,
        private val sessionService: SessionService,
        private val activePanelService: ActivePanelService,
        private val accountRepository: AccountRepository
) {
    private val code = "TRANSLATOR-APPLY"
    private val user get() = sessionService.session

    fun getApplyList(page: Int) =
            agendaRepository.findAllByCodeOrderByStatusAscAgendaNoDesc(code, PageRequest.of(page, 30)).map { TranslatorApplyDto(it) }

    fun getApplyCount() = mapOf("count" to agendaRepository.countByCodeAndStatus(code, "ACT"))

    fun getApply(applyNo: Long) =
            agendaRepository.findWithPollsByAgendaNoAndCode(applyNo, code)
                    ?.let { TranslatorApplyDto(it, true) }
                    ?: TranslatorApplyDto()

    fun createApply(translatorApplyRequest: TranslatorApplyRequest): ResultData<Long> {
        translatorApplyRequest.validate()
        if (user?.isManager() == true) {
            return ResultData("FAIL", "이미 권한이 있습니다.")
        }

        if (existDoingApply()) {
            return ResultData("FAIL", "신청중인 진행사항이 있습니다.")
        }

        if (agendaRepository.existsByCodeAndStatusAndAnAndUpdDtAfter(code, "DONE", user!!.an, LocalDateTime.now().minusDays(7))) {
            return ResultData("FAIL", "심사완료 일주일 후부터 재심사를 요청할 수 있습니다.")
        }

        agendaRepository.saveAndFlush(Agenda(
                code = code,
                status = "ACT",
                an = user!!.an,
                data1 = "ACT",
                data2 = user!!.name,
                data3 = translatorApplyRequest.website,
        )).run { return ResultData("OK", "", agendaNo) }
    }

    fun createApplyPoll(applyNo: Long, translatorApplyPollRequest: TranslatorApplyPollRequest): ResultStatus {
        var point = translatorApplyPollRequest.point.toInt()
        val comment = translatorApplyPollRequest.comment
        var app = agendaRepository.findByIdOrNull(applyNo)?.takeIf { it.code == code }
                ?: return ResultStatus("FAIL", "존재하지 않는 신청입니다.")

        app.takeIf { it.status == "ACT" }
                ?: return ResultStatus("FAIL", "종료된 신청서입니다.")

        if (! (user!!.isManager() || (user!!.an == app.an && point == 0))) {
            return ResultStatus("FAIL", "권한이 없습니다.")
        }

        val polls = app.polls
        if (point != 0) {
            if (polls.filter { it.an == user!!.an }.map { it.voteUp + it.voteDown }.sum() != 0) {
                return ResultStatus("FAIL", "찬성/반대는 한 신청처에 한번만 할 수 있습니다.")
            }
        }

        if (user!!.isRoot()) {
            point *= 10
        }
        val poll = agendaPollRepository.save(AgendaPoll(
                agenda = app,
                voteUp = if (point > 0) point else 0,
                voteDown = if (point < 0) point else 0,
                name = user!!.name,
                an = user!!.an,
                comment = comment
        ))

        val vote = (polls + poll).map { it.voteUp + it.voteDown }.sum()
        if (vote >= 3) {
            app.status = "DONE"
            app.data1 = "PASS"
            val account = accountRepository.findByIdOrNull(app.an)!!
            account.roles.add(AccountRole.TRANSLATOR)
            accountRepository.save(account)
            activePanelService.saveText("[${account.name}]님이 자막제작자로 참여하였습니다.", true)
            agendaPollRepository.save(toApplySystemPoll(app, "조건이 충족되어 권한이 부여되었습니다."))
        } else if (vote <= -3) {
            app.status = "DONE"
            app.data1 = "FAIL"
            agendaPollRepository.save(toApplySystemPoll(app, "최종 반려되었습니다. (7일 후 재신청이 가능합니다.)"))
        }
        agendaRepository.save(app.apply { updDt = LocalDateTime.now() })

        return ResultStatus("OK", "")
    }

    fun existDoingApply() = agendaRepository.existsByCodeAndStatusAndAn(code, "ACT", user!!.an)

    private fun toApplySystemPoll(agenda: Agenda, comment: String) = AgendaPoll(
            agenda = agenda,
            name = "",
            an = 0,
            comment = comment,
    )

}
