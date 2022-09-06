package anissia.configruration

import anissia.rdb.entity.AccountRole
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
            .authorizeRequests().antMatchers(HttpMethod.POST, "/api/active-panel/**").hasAnyRole(ROOT, TRANSLATOR).and()

            // board
            .authorizeRequests().antMatchers(HttpMethod.GET, "/api/board/**").permitAll().and()
            .authorizeRequests().antMatchers(HttpMethod.POST, "/api/board/**").authenticated().and()
            .authorizeRequests().antMatchers(HttpMethod.PUT, "/api/board/**").authenticated().and()
            .authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/board/**").authenticated().and()

            // login
            .authorizeRequests().antMatchers("/api/session").permitAll().and()
            .authorizeRequests().antMatchers("/api/session/**").permitAll().and()

            // account
            .authorizeRequests().antMatchers("/api/account/register").permitAll().and()
            .authorizeRequests().antMatchers("/api/account/recover").permitAll().and()
            .authorizeRequests().antMatchers("/api/account/recover/**").permitAll().and()

            // account user
            .authorizeRequests().antMatchers("/api/account/**").authenticated().and()

            // translator
            .authorizeRequests().antMatchers(HttpMethod.GET, "/api/translator/**").permitAll().and()
            .authorizeRequests().antMatchers(HttpMethod.POST, "/api/translator/**").authenticated().and()

            // Legacy
            .authorizeRequests().antMatchers("/anitime/list_img").permitAll().and()
            .authorizeRequests().antMatchers("/anitime/list").permitAll().and()
            .authorizeRequests().antMatchers("/anitime/cap").permitAll().and()
            .authorizeRequests().antMatchers("/anitime/list.js").permitAll().and()
            .authorizeRequests().antMatchers("/anitime/cap.js").permitAll().and()

            // data (test-data, data-normalize)
            .authorizeRequests().antMatchers("/data/**").permitAll().and()

            // admin
            .authorizeRequests().antMatchers("/api/admin/**").hasAnyRole(ROOT, TRANSLATOR).and()

            .authorizeRequests().antMatchers("/**").hasAnyRole(ROOT);
    }
}
