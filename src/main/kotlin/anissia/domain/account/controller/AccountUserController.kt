package anissia.domain.account.controller


import anissia.domain.account.command.EditUserNameCommand
import anissia.domain.account.command.EditUserPasswordCommand
import anissia.domain.account.model.AccountUserItem
import anissia.domain.account.service.UserService
import anissia.infrastructure.common.sessionItem
import anissia.shared.ApiResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/account/user")
class AccountUserController(
    private val userService: UserService,
) {
    @GetMapping
    fun getUser(exchange: ServerWebExchange): Mono<ApiResponse<AccountUserItem>> =
        userService.get(exchange.sessionItem)

    @PutMapping("/password")
    fun editUserPassword(@RequestBody cmd: EditUserPasswordCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        userService.editPassword(cmd, exchange.sessionItem)

    @PutMapping("/name")
    fun editUserName(@RequestBody cmd: EditUserNameCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        userService.editName(cmd, exchange.sessionItem)
}
