package anissia.domain.board.core.service

import anissia.domain.board.core.model.EditPostCommand
import anissia.domain.board.core.ports.inbound.EditPost
import anissia.domain.board.core.ports.outbound.BoardPostRepository
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EditPostService(
    private val boardPostRepository: BoardPostRepository,
): EditPost {
    @Transactional
    override fun handle(cmd: EditPostCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateLogin()

        return boardPostRepository
            .findByIdOrNull(cmd.postNo)
            ?.takeIf { !it.root && it.an == session.an }
            ?.let {
                boardPostRepository.save(it.apply { edit(cmd.content) })
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("권한이 없거나 존재하지 않는 글입니다.")
    }

}
