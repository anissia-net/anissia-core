package anissia.domain.board.service

import anissia.domain.board.command.EditPostCommand
import anissia.domain.board.repository.BoardPostRepository
import anissia.domain.session.model.Session
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
