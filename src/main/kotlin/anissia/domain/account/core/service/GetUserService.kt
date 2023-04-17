package anissia.domain.account.core.service

import anissia.domain.account.core.model.AccountUserItem
import anissia.domain.account.core.ports.inbound.GetUser
import anissia.domain.account.core.ports.outbound.AccountRepository
import anissia.domain.session.core.model.Session
import org.springframework.stereotype.Service

@Service
class GetUserService(
    private val accountRepository: AccountRepository,
): GetUser {
    override fun handle(session: Session): AccountUserItem =
        AccountUserItem.cast(accountRepository.findWithRolesByAn(session.an)!!)

}
