package anissia.domain.session.core.model

import anissia.domain.account.Account
import anissia.domain.account.model.AccountRole
import com.fasterxml.jackson.annotation.JsonIgnore
import gs.shared.FailException

class Session (
        val an: Long = 0,
        val name: String = "",
        val email: String = "",
        val roles: List<String> = listOf(),
        val ip: String = "",
) {
    companion object {
        fun cast(account: Account, ip: String) = Session(
            an = account.an,
            name = account.name,
            email = account.email,
            roles = account.roles.map { it.name },
            ip = ip
        )
    }

    fun validateLogin() {
        if (!isLogin) throw FailException("로그인이 필요합니다.")
    }

    fun validateAdmin() {
        if (!isAdmin) throw FailException("권한이 없습니다.")
    }

    fun validateRoot() {
        if (!isRoot) throw FailException("권한이 없습니다.")
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
