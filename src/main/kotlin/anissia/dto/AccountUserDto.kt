package anissia.dto

import anissia.rdb.entity.Account
import anissia.rdb.entity.AccountRole
import java.time.LocalDateTime

data class AccountUserDto (
    var email: String = "",
    var name: String = "",
    var regDt: LocalDateTime = LocalDateTime.now(),
    var roles: Set<AccountRole> = setOf(),
) {
    companion object {
        fun cast(account: Account) = AccountUserDto(
            email = account.email,
            name = account.name,
            regDt = account.regDt,
            roles = account.roles
        )
    }
}