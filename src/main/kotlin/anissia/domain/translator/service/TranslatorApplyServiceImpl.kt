package anissia.domain.translator.service

import anissia.domain.account.AccountRole
import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.service.ActivePanelService
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
import anissia.shared.ApiFailException
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
    override fun get(cmd: GetApplyCommand): Mono<TranslatorApplyItem> =
        Mono.fromCallable {
            cmd.validate()
            agendaRepository.findWithPollsByAgendaNoAndCode(cmd.applyNo, ApplyValue.CODE)
                ?.let { TranslatorApplyItem(it, true) }
                ?: TranslatorApplyItem()
        }

    override fun getList(cmd: GetApplyListCommand): Mono<Page<TranslatorApplyItem>> =
        Mono.fromCallable {
            cmd.validate()
            agendaRepository.findAllByCodeOrderByStatusAscAgendaNoDesc(ApplyValue.CODE, PageRequest.of(cmd.page, 30)).map { TranslatorApplyItem(it) }
        }

    override fun getApplyingCount(): Mono<Int> =
        Mono.just(agendaRepository.countByCodeAndStatus(ApplyValue.CODE, "ACT"))


    override fun getGrantedTime(an: Long): Mono<OffsetDateTime> =
        Mono.justOrEmpty(agendaRepository.findPassedTranslatorApply(an).firstOrNull()?.updDt)

    override fun isApplying(sessionItem: SessionItem): Mono<Boolean> =
        Mono.just(agendaRepository.existsByCodeAndStatusAndAn(ApplyValue.CODE, "ACT", sessionItem.an))

    @Transactional
    override fun add(cmd: AddApplyCommand, sessionItem: SessionItem): Mono<Long> =
        Mono.defer {
            cmd.validate()
            sessionItem.validateLogin()

            if (sessionItem.isAdmin) {
                return@defer Mono.error(ApiFailException("이미 권한이 있습니다."))
            }

            isApplying(sessionItem)
                .flatMap {
                    if (it) {
                        Mono.error(ApiFailException("신청중인 진행사항이 있습니다."))
                    } else {
                        Mono.just(true)
                    }
                }
                .flatMap {
                    if (agendaRepository.existsByCodeAndStatusAndAnAndUpdDtAfter(ApplyValue.CODE, "DONE", sessionItem.an, OffsetDateTime.now().minusDays(7))) {
                        Mono.error(ApiFailException("심사완료 일주일 후부터 재심사를 요청할 수 있습니다."))
                    } else {
                        agendaRepository.save(
                            Agenda(
                                code = ApplyValue.CODE,
                                status = "ACT",
                                an = sessionItem.an,
                                data1 = "ACT",
                                data2 = sessionItem.name,
                                data3 = cmd.website,
                            )
                        ).run { Mono.just(agendaNo) }
                    }
                }
        }


    @Transactional
    override fun addPoll(cmd: NewApplyPollCommand, sessionItem: SessionItem): Mono<String> =
        Mono.defer {
            cmd.validate()
            sessionItem.validateAdmin()

            var point = cmd.point.toInt()

            val app = agendaRepository.findByIdOrNull(cmd.applyNo)?.takeIf { it.code == ApplyValue.CODE }
                ?: return@defer Mono.error(ApiFailException("존재하지 않는 신청입니다."))

            app.takeIf { it.status == "ACT" }
                ?: return@defer Mono.error(ApiFailException("종료된 신청서입니다."))

            val polls = app.polls
            if (point != 0) {
                if (polls.filter { it.an == sessionItem.an }.any { it.vote != 0 }) {
                    return@defer Mono.error(ApiFailException("찬성/반대는 한 신청처에 한번만 할 수 있습니다."))
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
                activePanelService.addText(false, "[${account.name}]님이 자막제작자로 참여하였습니다.", null)
                    .map { agendaPollRepository.save(toApplySystemPoll(app, "조건이 충족되어 권한이 부여되었습니다.")); "" }
            } else if (vote <= -3) {
                app.status = "DONE"
                app.data1 = "FAIL"
                Mono.fromCallable {
                    agendaPollRepository.save(toApplySystemPoll(app, "최종 반려되었습니다. (7일 후 재신청이 가능합니다.)"))
                    ""
                }
            } else {
                Mono.just("")
            }.doOnNext { agendaRepository.save(app.apply { updDt = OffsetDateTime.now() }) }
        }

    private fun toApplySystemPoll(agenda: Agenda, comment: String) = AgendaPoll(
        agenda = agenda,
        name = "",
        an = 0,
        comment = comment,
    )
}
