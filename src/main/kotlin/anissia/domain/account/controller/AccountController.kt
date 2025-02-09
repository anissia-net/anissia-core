package anissia.domain.account.controller


import anissia.domain.account.command.*
import anissia.domain.account.service.RecoverPasswordService
import anissia.domain.account.service.RegisterServiceImpl
import anissia.infrastructure.common.sessionItem
import anissia.shared.ApiResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/account")
class AccountController(
    private val recover: RecoverPasswordService,
    private val register: RegisterServiceImpl,
) {
    @PostMapping("/register")
    fun register(@RequestBody cmd: RequestRegisterCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        register.request(cmd, exchange.sessionItem)

    @PutMapping("/register")
    fun registerValidation(@RequestBody cmd: CompleteRegisterCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        register.complete(cmd)

    @PostMapping("/recover")
    fun recover(@RequestBody cmd: RequestRecoverPasswordCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        recover.request(cmd, exchange.sessionItem)

    @PutMapping("/recover")
    fun recoverValidation(@RequestBody cmd: ValidateRecoverPasswordCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        recover.validate(cmd)

    @PutMapping("/recover/password")
    fun recoverPassword(@RequestBody cmd: CompleteRecoverPasswordCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        recover.complete(cmd)
}
