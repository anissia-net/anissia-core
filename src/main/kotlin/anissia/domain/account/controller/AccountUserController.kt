package anissia.domain.account.controller


import anissia.domain.account.command.EditUserNameCommand
import anissia.domain.account.command.EditUserPasswordCommand
import anissia.domain.account.service.UserService
import anissia.infrastructure.common.As
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/account/user")
class AccountUserController(
    private val userService: UserService,
) {
    @GetMapping
    fun getUser(exchange: ServerWebExchange) =
        userService.get(As.toSession(exchange))

    @PutMapping("/password")
    fun editUserPassword(@RequestBody cmd: EditUserPasswordCommand, exchange: ServerWebExchange) =
        userService.editPassword(cmd, As.toSession(exchange))

    @PutMapping("/name")
    fun editUserName(@RequestBody cmd: EditUserNameCommand, exchange: ServerWebExchange) =
        userService.editName(cmd, As.toSession(exchange))
}
