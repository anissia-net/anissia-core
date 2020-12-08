package anissia.misc

import org.springframework.security.core.GrantedAuthority

class PrefixRoleGrantedAuthority(
        private val role: String
): GrantedAuthority {
    override fun getAuthority() = "ROLE_$role"
}
