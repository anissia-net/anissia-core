package anissia.configruration

import anissia.domain.AccountRole
import me.saro.kit.bytes.Bytes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.MessageDigest


@Configuration
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    companion object {
        private val TRANSLATOR = AccountRole.TRANSLATOR.name
        private val ROOT = AccountRole.ROOT.name
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
        // disable csrf
        http.csrf().disable()
            // authorize requests after ignore resource setting
            .authorizeRequests().antMatchers(HttpMethod.GET, "/api/anime/schedule/**").permitAll().and()
            .authorizeRequests().antMatchers(HttpMethod.GET, "/api/anime/caption/animeNo/**").permitAll().and()

            .authorizeRequests().antMatchers(HttpMethod.GET, "/mig").permitAll().and()
            .authorizeRequests().antMatchers(HttpMethod.GET, "/rank").permitAll().and()

            .authorizeRequests().antMatchers("/**").hasAnyRole(ROOT);

//                // authorize requests after ignore resource setting
//                .authorizeRequests().antMatchers(
//                        "/api/lobby/**",
//                        "/api/anime/**",
//                        "/api/timetable/**",
//                        "/api/auth/**",
//                        "/api/board/any/**",
//                        "/api/asl/any/**",
//                        "/mig"
//                ).permitAll().and()
//
//                .authorizeRequests().antMatchers(
//                        "/api/user/**",
//                        "/api/asl/user/**",
//                        "/api/board/user/**"
//                ).authenticated().and()
//
//                .authorizeRequests().antMatchers(
//                        "/api/manage/**",
//                        "/api/asl/**"
//                ).hasAnyRole(TRANSLATOR, ROOT).and()
//
//                .authorizeRequests().antMatchers(
//                        "/**"
//                ).hasAnyRole(ROOT)
    }
}
