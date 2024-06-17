package test.domain.account


import anissia.domain.account.core.ports.inbound.EditUserName
import anissia.domain.account.core.ports.inbound.EditUserPassword
import anissia.domain.account.core.ports.inbound.GetUser

class AccountUserTest(
    private val editUserName: EditUserName,
    private val editUserPassword: EditUserPassword,
    private val getUser: GetUser,
) {
//    @GetMapping
//    fun getUser(exchange: ServerWebExchange) =
//        getUser.handle(As.toSession(exchange))
//
//    @PutMapping("/password")
//    fun editUserPassword(@RequestBody cmd: EditUserPasswordCommand, exchange: ServerWebExchange) =
//        editUserPassword.handle(cmd, As.toSession(exchange))
//
//    @PutMapping("/name")
//    fun editUserName(@RequestBody cmd: EditUserNameCommand, exchange: ServerWebExchange) =
//        editUserName.handle(cmd, As.toSession(exchange))
}
