package anissia.domain.account.core.service

import anissia.domain.account.core.model.RecoverValidationCommand
import anissia.domain.account.core.repository.AccountRecoverAuthRepository
import anissia.shared.ResultWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class RecoverValidationService(
    private val accountRecoverAuthRepository: AccountRecoverAuthRepository,
): RecoverValidation {

    @Transactional
    override fun handle(cmd: RecoverValidationCommand): ResultWrapper<Unit> {
        cmd.validate()

        return accountRecoverAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(cmd.tn, cmd.token, OffsetDateTime.now())
            ?.let { ResultWrapper.ok() }
            ?: ResultWrapper.fail("")
    }

}
