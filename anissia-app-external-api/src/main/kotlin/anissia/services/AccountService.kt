package anissia.services

import anissia.configruration.logger
import anissia.dto.request.AccountUpdateNameRequest
import anissia.dto.request.AccountUpdatePasswordRequest
import anissia.rdb.repository.AccountRepository
import org.slf4j.Logger
import org.springframework.stereotype.Service

/**
 *
 */
@Service
class AccountService (
    private val sessionService: SessionService,
    private val accountRepository: AccountRepository
) {
    private val log: Logger = logger<AccountService>()
    private val an get() = sessionService.session!!.an

    fun getUser() = accountRepository.findWithRolesByAn(an)

    fun updateUserPassword(accountUpdatePasswordRequest: AccountUpdatePasswordRequest) {

    }

    fun updateUserName(accountUpdateNameRequest: AccountUpdateNameRequest) {

    }
}
