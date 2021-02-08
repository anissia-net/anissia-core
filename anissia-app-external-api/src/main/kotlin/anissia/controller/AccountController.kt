package anissia.controller

import anissia.rdb.dto.request.AccountRecoverPasswordRequest
import anissia.rdb.dto.request.AccountRecoverRequest
import anissia.rdb.dto.request.EmailAuthTokenRequest
import anissia.rdb.dto.request.AccountRegisterRequest
import anissia.services.AccountRecoverService
import anissia.services.AccountRegisterService
import anissia.services.AccountService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/account")
class AccountController(
        private val accountService: AccountService,
        private val accountRegisterService: AccountRegisterService,
        private val accountRecoverService: AccountRecoverService
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody accountRegisterRequest: AccountRegisterRequest) =
        accountRegisterService.register(accountRegisterRequest)

    @PutMapping("/register")
    fun registerValidation(@Valid @RequestBody emailAuthTokenRequest: EmailAuthTokenRequest) =
        accountRegisterService.registerValidation(emailAuthTokenRequest)

    @PostMapping("/recover")
    fun recover(@Valid @RequestBody accountRecoverRequest: AccountRecoverRequest) =
        accountRecoverService.recover(accountRecoverRequest)

    @PutMapping("/recover")
    fun recoverValidation(@Valid @RequestBody emailAuthTokenRequest: EmailAuthTokenRequest) =
        accountRecoverService.recoverValidation(emailAuthTokenRequest)

    @PutMapping("/recover/password")
    fun recoverPassword(@Valid @RequestBody accountRecoverPasswordRequest: AccountRecoverPasswordRequest) =
        accountRecoverService.recoverPassword(accountRecoverPasswordRequest)

    // temp: 운영진들이 계정을 찾을 때 까지만
    @GetMapping("/recover/email/{name}")
    fun recoverByName(@PathVariable name: String) = accountRecoverService.findEmailByName(name)
}
