package anissia.domain.account.controller


import anissia.domain.account.command.*
import anissia.domain.account.service.RecoverPasswordService
import anissia.domain.account.service.RegisterService
import anissia.infrastructure.common.sessionItem
import anissia.infrastructure.common.toApiResponse
import anissia.shared.ApiResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/account")
class AccountController(
    private val recover: RecoverPasswordService,
    private val register: RegisterService,
) {
    @PostMapping("/register")
    fun register(@RequestBody cmd: RequestRegisterCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        register.request(cmd, exchange.sessionItem).toApiResponse

    @PutMapping("/register")
    fun registerValidation(@RequestBody cmd: CompleteRegisterCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        register.complete(cmd).toApiResponse

    @PostMapping("/recover")
    fun recover(@RequestBody cmd: RequestRecoverPasswordCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        recover.request(cmd, exchange.sessionItem).toApiResponse

    @PutMapping("/recover")
    fun recoverValidation(@RequestBody cmd: ValidateRecoverPasswordCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        recover.validate(cmd).toApiResponse

    @PutMapping("/recover/password")
    fun recoverPassword(@RequestBody cmd: CompleteRecoverPasswordCommand, exchange: ServerWebExchange): Mono<ApiResponse<String>> =
        recover.complete(cmd).toApiResponse
}
