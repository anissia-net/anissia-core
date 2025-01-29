package anissia.domain.activePanel.service

import anissia.domain.account.Account
import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.command.GetListActivePanelCommand
import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.board.BoardPost
import anissia.domain.board.BoardTopic
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.As
import anissia.shared.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivePanelLogServiceImpl(
    private val activePanelRepository: ActivePanelRepository,
): ActivePanelLogService {
    override fun getList(cmd: GetListActivePanelCommand, sessionItem: SessionItem): Mono<Page<ActivePanelItem>> {
        cmd.validate()
        return activePanelRepository.findAllByOrderByApNoDesc(PageRequest.of(cmd.page, 20))
            .run { if (cmd.mode == "admin" && sessionItem.isAdmin) this else As.filterPage(this) { it.published } }
            .map { ActivePanelItem(it) }
    }


    /*

class DoCommandActivePanelCommand(
var query: String = ""
) {
val published get() = query.startsWith("!")
val commend get() = query.startsWith("/")

val text get() = when {
    published -> query.substring(1)
    commend -> query.substring(1)
    else -> query
}

fun validate() {
    require(query.isNotBlank()) { "내용을 입력해주세요." }
}
}

 */

    override fun doCommand(cmd: String, sessionItem: SessionItem): Mono<Void> {

    }

    fun addText(published: Boolean, text: String, sessionItem: SessionItem?): Mono<ActivePanel> =
        activePanelRepository.save(ActivePanel(
            published = published,
            code = "TEXT",
            an = sessionItem?.an ?: 0,
            data1 = text
        ))

    fun addNotice(published: Boolean, text: String, sessionItem: SessionItem): Mono<ActivePanel> =
        activePanelRepository.save(ActivePanel(
            published = published,
            an = sessionItem.an,
            code = "TEXT",
            data1 = "《공지》 ${sessionItem.name} : $text"))


    fun addDeleteTopic(topic: BoardTopic, post: BoardPost, account: Account?, sessionItem: SessionItem): Mono<ActivePanel> =
        activePanelRepository.save(ActivePanel(
            published = false,
            code = "DEL",
            an = sessionItem.an,
            data1 = "[${sessionItem.name}]님이 글을 삭제했습니다.",
            data2 = "작성자/회원번호: ${account?.name?:"탈퇴회원"}/${account?.an?:-1}",
            data3 = "${topic.topic}\n${post.content}"
        ))

    fun addDeletePost(post: BoardPost, account: Account?, sessionItem: SessionItem): Mono<ActivePanel> =
        activePanelRepository.save(ActivePanel(
            published = false,
            code = "DEL",
            an = sessionItem.an,
            data1 = "[${sessionItem.name}]님이 댓글을 삭제했습니다.",
            data2 = "작성자/회원번호: ${account?.name?:"탈퇴회원"}/${account?.an?:-1}",
            data3 = post.content
        ))
}
