package anissia.domain.account.core.model

import anissia.domain.account.core.Account
import anissia.domain.account.core.AccountRole

data class AccountUserItem (
    var email: String = "",
    var name: String = "",
    val regTime: Long = 0L,
    var roles: Set<AccountRole> = setOf(),
) {
    companion object {
        fun cast(account: Account) = AccountUserItem(
            email = account.email,
            name = account.name,
            regTime = account.regDt.toEpochSecond(),
            roles = account.roles
        )
    }
}
