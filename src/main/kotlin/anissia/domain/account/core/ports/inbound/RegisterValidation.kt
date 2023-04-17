package anissia.domain.account.core.ports.inbound

import anissia.domain.account.core.model.RegisterValidationCommand
import anissia.shared.ResultWrapper

interface RegisterValidation {
    fun handle(cmd: RegisterValidationCommand): ResultWrapper<Unit>
}
