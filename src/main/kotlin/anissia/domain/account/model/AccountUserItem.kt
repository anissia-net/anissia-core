package anissia.domain.account.model

import anissia.domain.account.Account

class AccountUserItem (
    val email: String,
    val name: String,
    val regTime: Long,
    val roles: Set<AccountRole>,
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
