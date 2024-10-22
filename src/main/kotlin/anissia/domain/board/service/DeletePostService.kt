package anissia.domain.board.service

import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.board.core.model.DeletePostCommand
import anissia.domain.board.core.repository.BoardPostRepository
import anissia.domain.board.core.repository.BoardTopicRepository
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePostService(
    private val boardPostRepository: BoardPostRepository,
    private val boardTopicRepository: BoardTopicRepository,
    private val activePanelRepository: ActivePanelRepository,
): DeletePost {
    @Transactional
    override fun handle(cmd: DeletePostCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateLogin()

        return boardPostRepository
            .findByIdOrNull(cmd.postNo)
            ?.takeIf { !it.root && (it.an == session.an || session.isAdmin) }
            ?.let {
                if (it.an != session.an) {
                    activePanelRepository.save(
                        ActivePanel(
                            published = false,
                            code = "DEL",
                            an = session.an,
                            data1 = "[${session.name}]님이 댓글을 삭제했습니다.",
                            data2 = "작성자/회원번호: ${it.account?.name}/${it.an}",
                            data3 = it.content,
                        )
                    )
                }
                boardPostRepository.delete(it)
                boardTopicRepository.updatePostCount(it.topicNo)
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.")
    }
}
