package anissia.domain.activePanel.service

import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.command.*
import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.filterPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivePanelServiceImpl(
    private val activePanelRepository: ActivePanelRepository,
): ActivePanelService {
    override fun getList(cmd: GetListActivePanelCommand, sessionItem: SessionItem): Mono<Page<ActivePanelItem>> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .flatMap { activePanelRepository.findAllByOrderByApNoDesc(PageRequest.of(it.page, 20)) }
            .map { page -> if (cmd.mode == "admin" && sessionItem.isAdmin) page else page.filterPage { item -> item.published } }
            .map { page -> page.map { ActivePanelItem(it) } }

    override fun doCommand(cmd: DoCommandActivePanelCommand, sessionItem: SessionItem): Mono<String> =
        Mono.just(cmd)
            .doOnNext { it.validate() }
            .flatMap {
                if (it.commend) {
                    doCommandExecute(it, sessionItem)
                } else {
                    addNotice(AddTextActivePanelCommand(it.published, it.text), sessionItem)
                }
            }.map { "" }

    override fun addText(cmd: AddTextActivePanelCommand, sessionItem: SessionItem?): Mono<ActivePanel> =
        activePanelRepository.save(ActivePanel(
            published = cmd.published,
            code = "TEXT",
            an = sessionItem?.an ?: 0,
            data1 = cmd.text
        ))

    override fun addNotice(cmd: AddTextActivePanelCommand, sessionItem: SessionItem): Mono<ActivePanel> =
        activePanelRepository.save(ActivePanel(
            published = cmd.published,
            an = sessionItem.an,
            code = "TEXT",
            data1 = "《공지》 ${sessionItem.name} : ${cmd.text}"))


    override fun addDeleteTopic(cmd: AddDeleteTopicLogActivePanelCommand, sessionItem: SessionItem): Mono<ActivePanel> =
        activePanelRepository.save(ActivePanel(
            published = false,
            code = "DEL",
            an = sessionItem.an,
            data1 = "[${sessionItem.name}]님이 글을 삭제했습니다.",
            data2 = "작성자/회원번호: ${cmd.account?.name?:"탈퇴회원"}/${cmd.account?.an?:-1}",
            data3 = "${cmd.topic.topic}\n${cmd.post.content}"
        ))

    override fun addDeletePost(cmd: AddDeletePostLogActivePanelCommand, sessionItem: SessionItem): Mono<ActivePanel> =
        activePanelRepository.save(ActivePanel(
            published = false,
            code = "DEL",
            an = sessionItem.an,
            data1 = "[${sessionItem.name}]님이 댓글을 삭제했습니다.",
            data2 = "작성자/회원번호: ${cmd.account?.name?:"탈퇴회원"}/${cmd.account?.an?:-1}",
            data3 = cmd.post.content
        ))

    private fun doCommandExecute(cmd: DoCommandActivePanelCommand, sessionItem: SessionItem): Mono<String> =
        Mono.just(cmd).map { "" }

    /*
    @Transactional
    override fun doCommand(cmd: DoCommandActivePanelCommand, sessionItem: SessionItem): ApiResponse<Void> {
        cmd.validate()
        sessionItem.validateAdmin()

        if (cmd.commend) { // commend
            sessionItem.validateRoot()
            when {
                // drop permission
                cmd.query.startsWith("/권한반납 ") -> {
                    val name = cmd.query.substring(cmd.query.indexOf(' ') + 1)
                    val user = accountRepository.findByName(name)
                        ?: return ApiResponse.fail("존재하지 않는 회원입니다.")

                    if (user.isTranslator) {
                        // remove permission
                        user.roles.removeIf { it == AccountRole.TRANSLATOR }
                        accountRepository.save(user)
                        val deleteCount = captionService.delete(user, sessionItem)
                        sessionItem.addText("[${user.name}]님의 자막제작자 권한이 해지되었습니다.")
                        sessionItem.addText("[${user.name}]님의 모든 작품 ${deleteCount}개가 삭제되었습니다.")
                    } else {
                        return ApiResponse.fail("${user.name}님은 자막제작자 권한을 가지고 있지 않습니다.")
                    }
                }
                cmd.query == "/검색엔진 전체갱신" -> {
                    sessionItem.addText("[${sessionItem.name}]님이 검색엔진 reindex 작업을 시작했습니다.")
                    animeDocumentService.reset(false)
                    sessionItem.addText("검색엔진 reindex 작업이 완료되었습니다.")
                }
                cmd.query == "/검색엔진 초기화" -> {
                    sessionItem.addText("[${sessionItem.name}]님이 검색엔진 초기화 작업을 시작했습니다.")
                    animeDocumentService.reset(true)
                    sessionItem.addText("검색엔진 초기화 작업이 완료되었습니다.")
                }
                else -> return ApiResponse.fail("존재하지 않는 명령입니다.")
            }

        } else { // notice
            return sessionItem.addNotice(cmd)
        }
        return ApiResponse.ok()
    }
     */
}
