package anissia.domain.account.core.service

import anissia.domain.account.core.model.EditUserPasswordCommand
import anissia.domain.account.core.repository.AccountRepository
import anissia.domain.session.core.model.Session
import anissia.infrastructure.service.BCryptService
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class EditUserPasswordService(
    private val bCryptService: BCryptService,
    private val accountRepository: AccountRepository,
): EditUserPassword {
    override fun handle(cmd: EditUserPasswordCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateLogin()

        val account = accountRepository.findByIdOrNull(session.an)
            ?.takeIf { bCryptService.matches(it.password, cmd.oldPassword) }
            ?: return ResultWrapper.fail("기존 암호가 일치하지 않습니다.")

        account.password = bCryptService.encode(cmd.newPassword)
        accountRepository.save(account)
        return ResultWrapper.ok()
    }
}
