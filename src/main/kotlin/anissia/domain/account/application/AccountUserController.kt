package anissia.domain.account.application


import anissia.domain.account.core.model.EditUserNameCommand
import anissia.domain.account.core.model.EditUserPasswordCommand
import anissia.domain.account.core.service.EditUserName
import anissia.domain.account.core.service.EditUserPassword
import anissia.domain.account.core.service.GetUser
import anissia.infrastructure.common.As
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/account/user")
class AccountUserController(
    private val editUserName: EditUserName,
    private val editUserPassword: EditUserPassword,
    private val getUser: GetUser,
) {
    @GetMapping
    fun getUser(exchange: ServerWebExchange) =
        getUser.handle(As.toSession(exchange))

    @PutMapping("/password")
    fun editUserPassword(@RequestBody cmd: EditUserPasswordCommand, exchange: ServerWebExchange) =
        editUserPassword.handle(cmd, As.toSession(exchange))

    @PutMapping("/name")
    fun editUserName(@RequestBody cmd: EditUserNameCommand, exchange: ServerWebExchange) =
        editUserName.handle(cmd, As.toSession(exchange))
}
