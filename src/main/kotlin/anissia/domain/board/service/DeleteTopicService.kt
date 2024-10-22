package anissia.domain.board.service

import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.board.core.model.DeleteTopicCommand
import anissia.domain.board.core.repository.BoardPostRepository
import anissia.domain.board.core.repository.BoardTopicRepository
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteTopicService(
    private val boardPostRepository: BoardPostRepository,
    private val boardTopicRepository: BoardTopicRepository,
    private val activePanelRepository: ActivePanelRepository,
): anissia.domain.board.service.DeleteTopic {
    @Transactional
    override fun handle(cmd: DeleteTopicCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateLogin()

        return boardTopicRepository
            .findByIdOrNull(cmd.topicNo)
            ?.takeIf { it.an == session.an || session.isAdmin }
            ?.let {
                if (it.an != session.an) {
                    activePanelRepository.save(
                        ActivePanel(
                        published = false,
                        code = "DEL",
                        an = session.an,
                        data1 = "[${session.name}]님이 글을 삭제했습니다.",
                        data2 = "작성자/회원번호: ${it.account?.name}/${it.an}",
                        data3 = "${it.topic}\n${boardPostRepository.findWithAccountByTopicNoAndRootIsTrue(it.topicNo)?.content}",
                    )
                    )
                }
                boardPostRepository.deleteAllByTopicNo(cmd.topicNo)
                boardTopicRepository.delete(it)
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.")
    }

}
