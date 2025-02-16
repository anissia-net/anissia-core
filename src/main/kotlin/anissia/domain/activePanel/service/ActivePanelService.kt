package anissia.domain.activePanel.service

import anissia.domain.account.Account
import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.command.GetListActivePanelCommand
import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.board.BoardPost
import anissia.domain.board.BoardTopic
import anissia.domain.session.model.SessionItem
import org.springframework.data.domain.Page
import reactor.core.publisher.Mono

interface ActivePanelService {
    fun getList(cmd: GetListActivePanelCommand, sessionItem: SessionItem): Mono<Page<ActivePanelItem>>
    fun add(activePanel: ActivePanel): Mono<ActivePanel>

    fun addText(published: Boolean, text: String, sessionItem: SessionItem?): Mono<ActivePanel> =
        add(ActivePanel(published = published, code = "TEXT", an = sessionItem?.an ?: 0, data1 = text))

    fun addNotice(published: Boolean, text: String, sessionItem: SessionItem): Mono<ActivePanel> =
        add(ActivePanel(published = published, code = "TEXT", an = sessionItem.an, data1 = "《공지》 ${sessionItem.name} : $text"))

    fun addDeleteTopic(topic: BoardTopic, post: BoardPost, account: Account?, sessionItem: SessionItem): Mono<ActivePanel> =
        add(ActivePanel(
            published = false,
            code = "DEL",
            an = sessionItem.an,
            data1 = "[${sessionItem.name}]님이 글을 삭제했습니다.",
            data2 = "작성자/회원번호: ${account?.name?:"탈퇴회원"}/${account?.an?:-1}",
            data3 = "${topic.topic}\n${post.content}"
        ))

    fun addDeletePost(post: BoardPost, account: Account?, sessionItem: SessionItem): Mono<ActivePanel> =
        add(ActivePanel(
            published = false,
            code = "DEL",
            an = sessionItem.an,
            data1 = "[${sessionItem.name}]님이 댓글을 삭제했습니다.",
            data2 = "작성자/회원번호: ${account?.name?:"탈퇴회원"}/${account?.an?:-1}",
            data3 = post.content
        ))
}
