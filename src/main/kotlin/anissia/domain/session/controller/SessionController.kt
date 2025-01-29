package anissia.domain.session.controller

import anissia.domain.session.command.DoTokenLoginCommand
import anissia.domain.session.command.DoUserLoginCommand
import anissia.domain.session.model.JwtAuthInfoItem
import anissia.domain.session.service.JwtService
import anissia.domain.session.service.LoginService
import anissia.infrastructure.common.As
import anissia.shared.ApiResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/session")
class SessionController(
    private val loginService: LoginService,
    private val jwtService: JwtService,
) {
    @PostMapping
    fun doLogin(@RequestBody cmd: DoUserLoginCommand, exchange: ServerWebExchange): ApiResponse<JwtAuthInfoItem> =
        loginService.doUserLogin(cmd, exchange.sessionItem)

    @PostMapping("/token")
    fun doTokenLogin(@RequestBody cmd: DoTokenLoginCommand, exchange: ServerWebExchange): ApiResponse<JwtAuthInfoItem> =
        loginService.doTokenLogin(cmd, exchange.sessionItem)

    @PutMapping()
    fun updateAuthInfo(exchange: ServerWebExchange): ApiResponse<JwtAuthInfoItem> =
        jwtService.updateAuthInfo(exchange.sessionItem)
}
