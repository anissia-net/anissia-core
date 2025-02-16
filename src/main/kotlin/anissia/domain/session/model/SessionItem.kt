package anissia.domain.session.model

import anissia.domain.account.Account
import anissia.domain.account.AccountRole
import anissia.shared.ApiFailException
import com.fasterxml.jackson.annotation.JsonIgnore

class SessionItem (
        val an: Long = 0,
        val name: String = "",
        val email: String = "",
        val roles: List<String> = listOf(),
        val ip: String = "",
) {
    companion object {
        fun cast(account: Account, ip: String) = SessionItem(
            an = account.an,
            name = account.name,
            email = account.email,
            roles = account.roles.map { it.name },
            ip = ip
        )
    }

    fun validateLogin() {
        if (!isLogin) throw ApiFailException("로그인이 필요합니다.")
    }

    fun validateAdmin() {
        if (!isAdmin) throw ApiFailException("권한이 없습니다.")
    }

    fun validateRoot() {
        if (!isRoot) throw ApiFailException("권한이 없습니다.")
    }

    @get:JsonIgnore
    val isAdmin: Boolean get() = isLogin && (roles.contains(AccountRole.TRANSLATOR.name) || roles.contains(AccountRole.ROOT.name))
    @get:JsonIgnore
    val isTranslator: Boolean get() = isLogin && roles.contains(AccountRole.TRANSLATOR.name)
    @get:JsonIgnore
    val isRoot: Boolean get() = isLogin && roles.contains(AccountRole.ROOT.name)
    @get:JsonIgnore
    val isLogin: Boolean get() = an > 0L
}
