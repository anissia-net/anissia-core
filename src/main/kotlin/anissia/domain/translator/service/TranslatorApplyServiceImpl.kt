package anissia.domain.translator.service

import anissia.domain.account.AccountRole
import anissia.domain.account.repository.AccountRepository
import anissia.domain.agenda.Agenda
import anissia.domain.agenda.AgendaPoll
import anissia.domain.agenda.repository.AgendaPollRepository
import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.session.model.SessionItem
import anissia.domain.translator.command.AddApplyCommand
import anissia.domain.translator.command.GetApplyCommand
import anissia.domain.translator.command.GetApplyListCommand
import anissia.domain.translator.command.NewApplyPollCommand
import anissia.domain.translator.infrastructure.ApplyValue
import anissia.domain.translator.model.TranslatorApplyItem
import anissia.shared.ResultWrapper
import gs.shared.FailException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@Service
class TranslatorApplyServiceImpl(
    private val agendaRepository: AgendaRepository,
    private val agendaPollRepository: AgendaPollRepository,
    private val accountRepository: AccountRepository,
    private val activePanelService: ActivePanelService,
): TranslatorApplyService {
    override fun get(cmd: GetApplyCommand): Mono<TranslatorApplyItem> {
        cmd.validate()
        return agendaRepository.findWithPollsByAgendaNoAndCode(cmd.applyNo, ApplyValue.CODE)
            ?.let { TranslatorApplyItem(it, true) }
            ?: TranslatorApplyItem()
    }

    override fun getList(cmd: GetApplyListCommand): Mono<Page<TranslatorApplyItem>> {
        cmd.validate()
        return agendaRepository.findAllByCodeOrderByStatusAscAgendaNoDesc(ApplyValue.CODE, PageRequest.of(cmd.page, 30)).map { TranslatorApplyItem(it) }
    }

    override fun getApplyingCount(): Mono<Int> =
        agendaRepository.countByCodeAndStatus(ApplyValue.CODE, "ACT")

    override fun getGrantedTime(an: Long): Mono<OffsetDateTime> =
        agendaRepository.findPassedTranslatorApply(an).firstOrNull()?.updDt

    override fun isApplying(sessionItem: SessionItem): Mono<Boolean> {
        return agendaRepository.existsByCodeAndStatusAndAn(ApplyValue.CODE, "ACT", sessionItem.an)
    }

    @Transactional
    override fun add(cmd: AddApplyCommand, sessionItem: SessionItem): Mono<Long> {
        cmd.validate()
        sessionItem.validateLogin()

        if (sessionItem.isAdmin) {
            throw FailException("이미 권한이 있습니다.")
        }

        if (isApplying(sessionItem)) {
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
        ).run { return agendaNo) }
    }

    @Transactional
    override fun addPoll(cmd: NewApplyPollCommand, sessionItem: SessionItem): Mono<String> {
        cmd.validate()
        sessionItem.validateAdmin()

        var point = cmd.point.toInt()

        val app = agendaRepository.findByIdOrNull(cmd.applyNo)?.takeIf { it.code == ApplyValue.CODE }
            ?: return Mono.error(ApiFailException("존재하지 않는 신청입니다.")

        app.takeIf { it.status == "ACT" }
            ?: return Mono.error(ApiFailException("종료된 신청서입니다.")

        val polls = app.polls
        if (point != 0) {
            if (polls.filter { it.an == sessionItem.an }.any { it.vote != 0 }) {
                return Mono.error(ApiFailException("찬성/반대는 한 신청처에 한번만 할 수 있습니다.")
            }
        }

        if (sessionItem.isRoot) {
            point *= 10
        }
        val poll = agendaPollRepository.save(
            AgendaPoll(
                agenda = app,
                voteUp = if (point > 0) point else 0,
                voteDown = if (point < 0) point else 0,
                name = sessionItem.name,
                an = sessionItem.an,
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
            activePanelService.addText(AddTextActivePanelCommand("[${account.name}]님이 자막제작자로 참여하였습니다."), null)
            agendaPollRepository.save(toApplySystemPoll(app, "조건이 충족되어 권한이 부여되었습니다."))
        } else if (vote <= -3) {
            app.status = "DONE"
            app.data1 = "FAIL"
            agendaPollRepository.save(toApplySystemPoll(app, "최종 반려되었습니다. (7일 후 재신청이 가능합니다.)"))
        }
        agendaRepository.save(app.apply { updDt = OffsetDateTime.now() })

        return )
    }

    private fun toApplySystemPoll(agenda: Agenda, comment: String) = AgendaPoll(
        agenda = agenda,
        name = "",
        an = 0,
        comment = comment,
    )
}
