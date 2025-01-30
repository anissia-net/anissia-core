package anissia.domain.account.controller


import anissia.domain.account.command.*
import anissia.domain.account.service.RecoverPasswordService
import anissia.domain.account.service.RegisterServiceImpl
import anissia.infrastructure.common.As
import anissia.infrastructure.common.As.Companion.sessionItem
import anissia.infrastructure.common.As.Companion.toApiResponse
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
    fun register(@RequestBody cmd: RequestRegisterCommand, exchange: ServerWebExchange): Mono<ApiResponse<Void>> =
        register.request(cmd, exchange.sessionItem).toApiResponse

    @PutMapping("/register")
    fun registerValidation(@RequestBody cmd: CompleteRegisterCommand, exchange: ServerWebExchange): Mono<ApiResponse<Void>> =
        register.complete(cmd).toApiResponse

    @PostMapping("/recover")
    fun recover(@RequestBody cmd: RequestRecoverPasswordCommand, exchange: ServerWebExchange): Mono<ApiResponse<Void>> =
        recover.request(cmd, exchange.sessionItem).toApiResponse

    @PutMapping("/recover")
    fun recoverValidation(@RequestBody cmd: ValidateRecoverPasswordCommand, exchange: ServerWebExchange): Mono<ApiResponse<Void>> =
        recover.validate(cmd).toApiResponse

    @PutMapping("/recover/password")
    fun recoverPassword(@RequestBody cmd: CompleteRecoverPasswordCommand, exchange: ServerWebExchange): Mono<ApiResponse<Void>> =
        recover.complete(cmd).toApiResponse
}
