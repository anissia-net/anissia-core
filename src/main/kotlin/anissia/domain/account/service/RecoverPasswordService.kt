package anissia.domain.account.service

import anissia.domain.account.model.RecoverPasswordCommand
import anissia.domain.account.repository.AccountRecoverAuthRepository
import anissia.domain.account.repository.AccountRepository
import anissia.infrastructure.service.BCryptService
import anissia.shared.ResultWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class RecoverPasswordService(
    private val accountRecoverAuthRepository: AccountRecoverAuthRepository,
    private val accountRepository: AccountRepository,
    private val bCryptService: BCryptService,
): RecoverPassword {

    @Transactional
    override fun handle(cmd: RecoverPasswordCommand): ResultWrapper<Unit> {
        cmd.validate()

        val auth = accountRecoverAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(cmd.tn, cmd.token, OffsetDateTime.now())
            ?: return ResultWrapper.fail("이메일 인증이 만료되었습니다.")

        val account = auth.account
            ?: return ResultWrapper.fail("해당 메일인증에서 계정정보를 찾을 수 없습니다.")

        accountRecoverAuthRepository.save(auth.apply { usedDt = OffsetDateTime.now() })
        accountRepository.save(account.apply { password = bCryptService.encode(cmd.password) })

        return ResultWrapper.ok()
    }

}

