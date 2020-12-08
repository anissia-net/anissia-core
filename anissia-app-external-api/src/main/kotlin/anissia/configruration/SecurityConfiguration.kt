package anissia.configruration

import anissia.domain.AnissiaRole
import me.saro.kit.bytes.Bytes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.MessageDigest


@Configuration
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    companion object {
        private val TRANSLATOR = AnissiaRole.TRANSLATOR.name
        private val ADMIN = AnissiaRole.ADMIN.name
        private val ROOT = AnissiaRole.ROOT.name
    }

    // old anissia hash -> sha512(text + "$0")
    private val oaPasswordEncoder = object: PasswordEncoder {
        override fun matches(rawPassword: CharSequence, encodedPassword: String) = encodedPassword == encode(rawPassword)
        override fun encode(rawPassword: CharSequence)
                = Bytes.toHex(MessageDigest.getInstance("SHA-512").digest("${rawPassword}$0".toByteArray()))!!
    }

    @Bean
    fun passwordEncoder()
            = "bcrypt".let { DelegatingPasswordEncoder(it, mapOf(it to BCryptPasswordEncoder(), "oa" to oaPasswordEncoder)) }

    /**
     * security configure
     * SessionService is login / logout
     */
    override fun configure(http: HttpSecurity) {
        http
                // disable csrf
                .csrf().disable()

                // authorize requests after ignore resource setting
                .authorizeRequests().antMatchers(
                        "/api/lobby/**",
                        "/api/anime/**",
                        "/api/timetable/**",
                        "/api/auth/**",
                        "/api/board/any/**",
                        "/api/asl/any/**",
                        "/mig"
                ).permitAll().and()

                .authorizeRequests().antMatchers(
                        "/api/user/**",
                        "/api/asl/user/**",
                        "/api/board/user/**"
                ).authenticated().and()

                .authorizeRequests().antMatchers(
                        "/api/manage/**",
                        "/api/asl/**"
                ).hasAnyRole(TRANSLATOR, ADMIN, ROOT).and()

                .authorizeRequests().antMatchers(
                        "/**"
                ).hasAnyRole(ADMIN, ROOT)
    }
}
