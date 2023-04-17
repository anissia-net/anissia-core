package anissia.domain.session.application

import anissia.domain.session.core.model.DoLoginCommand
import anissia.domain.session.core.model.DoTokenLoginCommand
import anissia.domain.session.core.model.LoginInfoItem
import anissia.domain.session.core.ports.inbound.DoLogin
import anissia.domain.session.core.ports.inbound.DoTokenLogin
import anissia.domain.session.core.ports.inbound.UpdateJwt
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/session")
class SessionController(
    private val doLogin: DoLogin,
    private val doTokenLogin: DoTokenLogin,
    private val updateJwt: UpdateJwt,
) {
    @PostMapping
    fun doLogin(@RequestBody cmd: DoLoginCommand, exchange: ServerWebExchange): ResultWrapper<LoginInfoItem> =
        doLogin.handle(cmd, As.toSession(exchange))

    @PostMapping("/token")
    fun doTokenLogin(@RequestBody cmd: DoTokenLoginCommand, exchange: ServerWebExchange): ResultWrapper<LoginInfoItem> =
        doTokenLogin.handle(cmd, As.toSession(exchange))

    @PutMapping()
    fun updateJwt(exchange: ServerWebExchange): ResultWrapper<LoginInfoItem> =
        updateJwt.handle(As.toSession(exchange))
}
