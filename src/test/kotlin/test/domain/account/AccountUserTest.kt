package test.domain.account


import anissia.domain.account.service.UserService

class AccountUserTest(
    private val userService: UserService,
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
