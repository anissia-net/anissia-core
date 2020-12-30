package anissia.controller

import net.dhant.dto.request.LoginRequest
import net.dhant.dto.request.LoginTokenRequest
import net.dhant.dto.UserSession
import net.dhant.service.SessionService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/session")
class SessionController(
        private val sessionService: SessionService
) {
    @PostMapping
    fun login(@Valid @RequestBody loginRequest: LoginRequest) =
            sessionService.doLogin(loginRequest)

    @PostMapping("/token")
    fun tokenLogin(@Valid @RequestBody loginTokenRequest: LoginTokenRequest) =
            sessionService.doTokenLogin(loginTokenRequest)

    @DeleteMapping
    fun logout() = sessionService.doLogout()

    @GetMapping
    fun session() = sessionService.session?: UserSession()
}
