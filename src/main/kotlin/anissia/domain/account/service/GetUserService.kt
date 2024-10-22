package anissia.domain.account.service

import anissia.domain.account.model.AccountUserItem
import anissia.domain.account.repository.AccountRepository
import anissia.domain.session.model.Session
import org.springframework.stereotype.Service

@Service
class GetUserService(
    private val accountRepository: AccountRepository,
): GetUser {
    override fun handle(session: Session): AccountUserItem =
        AccountUserItem.cast(accountRepository.findWithRolesByAn(session.an)!!)

}
