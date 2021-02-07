package anissia.controller

import anissia.rdb.dto.request.RegisterEmailAuthTokenRequest
import anissia.rdb.dto.request.RegisterRequest
import anissia.services.AccountRegisterService
import anissia.services.AccountService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/account")
class AccountController(
        private val accountService: AccountService,
        private val accountRegisterService: AccountRegisterService
) {


    @PostMapping("/register")
    fun register(@Valid @RequestBody registerRequest: RegisterRequest) =
        accountRegisterService.register(registerRequest)

    @PutMapping("/register")
    fun registerValidation(@Valid @RequestBody registerEmailAuthTokenRequest: RegisterEmailAuthTokenRequest) =
        accountRegisterService.registerValidation(registerEmailAuthTokenRequest)
}
