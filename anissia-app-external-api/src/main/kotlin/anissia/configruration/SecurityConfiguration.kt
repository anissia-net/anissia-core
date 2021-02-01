package anissia.configruration

import anissia.rdb.domain.AccountRole
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

    private val oaPasswordEncoder = object: PasswordEncoder {
        override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean = encodedPassword == encode(rawPassword)
        override fun encode(rawPassword: CharSequence) = Bytes.toHex(MessageDigest.getInstance("SHA-512").digest("${rawPassword}$0".toByteArray()))!!
    }

    /**
     * !important
     * old anissia password hash type is sha512(password + "$0") alias {oa}
     * new anissia password hash type is {bcrypt}
     *
     * if the login is successful with the account password hash type is {oa}
     * the password hash type is changed to {bcrypt}
     *
     * @see
     * SecurityConfiguration.oaPasswordEncoder
     * SessionService.doLogin()
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder
            = "bcrypt".let { DelegatingPasswordEncoder(it, mapOf(it to BCryptPasswordEncoder(), "oa" to oaPasswordEncoder)) }

    /**
     * security configure
     * SessionService is login / logout
     */
    override fun configure(http: HttpSecurity) {
        // disable csrf
        http.csrf().disable()
            // anime
            .authorizeRequests().antMatchers(HttpMethod.GET, "/api/anime/**").permitAll().and()

            // active-panel
            .authorizeRequests().antMatchers(HttpMethod.GET, "/api/active-panel/**").permitAll().and()
            .authorizeRequests().antMatchers(HttpMethod.POST, "/api/active-panel/**").hasAnyRole(ROOT).and()

            // board
            .authorizeRequests().antMatchers(HttpMethod.GET, "/api/board/**").permitAll().and()

            // login
            .authorizeRequests().antMatchers("/api/session").permitAll().and()
            .authorizeRequests().antMatchers("/api/session/**").permitAll().and()

            // Legacy
            .authorizeRequests().antMatchers("/anitime/list_img").permitAll().and()

            // other
            .authorizeRequests().antMatchers(HttpMethod.GET, "/mig").permitAll().and()
            .authorizeRequests().antMatchers(HttpMethod.GET, "/rank").permitAll().and()

            // admin
            .authorizeRequests().antMatchers("/api/admin/**").hasAnyRole(ROOT, TRANSLATOR).and()

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
