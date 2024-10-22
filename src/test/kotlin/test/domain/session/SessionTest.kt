package test.domain.session

import anissia.domain.session.service.DoLogin
import anissia.domain.session.service.DoTokenLogin
import anissia.domain.session.service.UpdateJwt

class SessionTest(
    private val doLogin: DoLogin,
    private val doTokenLogin: DoTokenLogin,
    private val updateJwt: UpdateJwt,
) {
//    @PostMapping
//    fun doLogin(@RequestBody cmd: DoLoginCommand, exchange: ServerWebExchange): ResultWrapper<LoginInfoItem> =
//        doLogin.handle(cmd, As.toSession(exchange))
//
//    @PostMapping("/token")
//    fun doTokenLogin(@RequestBody cmd: DoTokenLoginCommand, exchange: ServerWebExchange): ResultWrapper<LoginInfoItem> =
//        doTokenLogin.handle(cmd, As.toSession(exchange))
//
//    @PutMapping()
//    fun updateJwt(exchange: ServerWebExchange): ResultWrapper<LoginInfoItem> =
//        updateJwt.handle(As.toSession(exchange))
}
