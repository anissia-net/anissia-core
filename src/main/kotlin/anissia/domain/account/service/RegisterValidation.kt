package anissia.domain.account.service

import anissia.domain.account.model.RegisterValidationCommand
import anissia.shared.ResultWrapper

interface RegisterValidation {
    fun handle(cmd: RegisterValidationCommand): ResultWrapper<Unit>
}
