package anissia.domain.activePanel.service

import anissia.domain.account.AccountRole
import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.command.DoCommandActivePanelCommand
import anissia.domain.activePanel.command.GetListActivePanelCommand
import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.anime.service.AnimeDocumentService
import anissia.domain.anime.service.CaptionService
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.filterPage
import anissia.shared.ApiFailException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivePanelServiceImpl(
    private val activePanelRepository: ActivePanelRepository,
    private val accountRepository: AccountRepository,
    private val animeDocumentService: AnimeDocumentService,
    private val captionService: CaptionService,
): ActivePanelService {

    override fun getList(cmd: GetListActivePanelCommand, sessionItem: SessionItem): Mono<Page<ActivePanelItem>> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .map {
                activePanelRepository.findAllByOrderByApNoDesc(PageRequest.of(cmd.page, 20))
                    .run { if (cmd.mode == "admin" && sessionItem.isAdmin) this else this.filterPage { it.published } }
            }
            .map { activePanel -> activePanel.map { ActivePanelItem(it) } }

    override fun add(activePanel: ActivePanel): Mono<ActivePanel> =
        Mono.just(activePanel)
            .map { activePanelRepository.save(it) }

    override fun doCommand(cmd: DoCommandActivePanelCommand, sessionItem: SessionItem): Mono<String> =
        Mono.just(cmd)
            .doOnNext { cmd.validate() }
            .doOnNext { sessionItem.validateAdmin() }
            .flatMap {
                if (cmd.commend) {
                    doExecuteCommand(cmd, sessionItem)
                } else {
                    addNotice(cmd.published, cmd.text, sessionItem).map { "" }
                }
            }

    private fun doExecuteCommand(cmd: DoCommandActivePanelCommand, sessionItem: SessionItem): Mono<String> =
        Mono.just(cmd)
            .doOnNext { sessionItem.validateRoot() }
            .flatMap {
                when {
                    // drop permission
                    cmd.query.startsWith("/권한반납 ") -> {
                        val name = cmd.query.substring(cmd.query.indexOf(' ') + 1)
                        val user = accountRepository.findByName(name)
                            ?: return@flatMap Mono.error(ApiFailException("존재하지 않는 회원입니다."))

                        if (user.isTranslator) {
                            accountRepository.save(user.apply { roles.removeIf { it == AccountRole.TRANSLATOR } })
                            val deleteCount = captionService.delete(user, sessionItem)
                            addText(false, "[${user.name}]님의 자막제작자 권한이 해지되었습니다.", sessionItem)
                                .flatMap { addText(false, "[${user.name}]님의 모든 작품 ${deleteCount}개가 삭제되었습니다.", sessionItem) }
                                .thenReturn("")
                        } else {
                            Mono.error(ApiFailException("${user.name}님은 자막제작자 권한을 가지고 있지 않습니다."))
                        }
                    }
                    cmd.query == "/검색엔진 전체갱신" -> {
                        addText(false, "[${sessionItem.name}]님이 검색엔진 reindex 작업을 시작했습니다.", sessionItem)
                            .flatMap { animeDocumentService.reset(false) }
                            .flatMap { addText(false, "검색엔진 reindex 작업이 완료되었습니다.", sessionItem) }
                            .thenReturn("")
                    }
                    cmd.query == "/검색엔진 초기화" -> {
                        addText(false, "[${sessionItem.name}]님이 검색엔진 초기화 작업을 시작했습니다.", sessionItem)
                            .flatMap { animeDocumentService.reset(true) }
                            .flatMap { addText(false, "검색엔진 초기화 작업이 완료되었습니다.", sessionItem) }
                            .thenReturn("")
                    }
                    else -> Mono.error(ApiFailException("존재하지 않는 명령입니다."))
                }
            }
}
