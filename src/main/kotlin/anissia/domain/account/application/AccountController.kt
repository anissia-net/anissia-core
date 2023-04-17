package anissia.domain.account.application


import anissia.domain.account.core.model.*
import anissia.domain.account.core.ports.inbound.*
import anissia.infrastructure.common.As
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/account")
class AccountController(
    private val recover: Recover,
    private val recoverPassword: RecoverPassword,
    private val recoverValidation: RecoverValidation,
    private val register: Register,
    private val registerValidation: RegisterValidation,
) {
    @PostMapping("/register")
    fun register(@RequestBody cmd: RegisterCommand, exchange: ServerWebExchange) =
        register.handle(cmd, As.toSession(exchange))

    @PutMapping("/register")
    fun registerValidation(@RequestBody cmd: RegisterValidationCommand, exchange: ServerWebExchange) =
        registerValidation.handle(cmd)

    @PostMapping("/recover")
    fun recover(@RequestBody cmd: RecoverCommand, exchange: ServerWebExchange) =
        recover.handle(cmd, As.toSession(exchange))

    @PutMapping("/recover")
    fun recoverValidation(@RequestBody cmd: RecoverValidationCommand, exchange: ServerWebExchange) =
        recoverValidation.handle(cmd)

    @PutMapping("/recover/password")
    fun recoverPassword(@RequestBody cmd: RecoverPasswordCommand, exchange: ServerWebExchange) =
        recoverPassword.handle(cmd)
}
