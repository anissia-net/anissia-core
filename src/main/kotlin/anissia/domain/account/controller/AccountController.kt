package anissia.domain.account.controller


import anissia.domain.account.model.*
import anissia.domain.account.service.RecoverPasswordService
import anissia.domain.account.service.RegisterServiceImpl
import anissia.infrastructure.common.As
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/account")
class AccountController(
    private val recover: RecoverPasswordService,
    private val register: RegisterServiceImpl,
) {
    @PostMapping("/register")
    fun register(@RequestBody cmd: RequestRegisterCommand, exchange: ServerWebExchange) =
        register.request(cmd, As.toSession(exchange))

    @PutMapping("/register")
    fun registerValidation(@RequestBody cmd: CompleteRegisterCommand, exchange: ServerWebExchange) =
        register.complete(cmd)

    @PostMapping("/recover")
    fun recover(@RequestBody cmd: RequestRecoverPasswordCommand, exchange: ServerWebExchange) =
        recover.request(cmd, As.toSession(exchange))

    @PutMapping("/recover")
    fun recoverValidation(@RequestBody cmd: ValidateRecoverPasswordCommand, exchange: ServerWebExchange) =
        recover.validate(cmd)

    @PutMapping("/recover/password")
    fun recoverPassword(@RequestBody cmd: CompleteRecoverPasswordCommand, exchange: ServerWebExchange) =
        recover.complete(cmd)
}
