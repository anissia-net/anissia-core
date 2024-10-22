package anissia.domain.account.core.service

import anissia.domain.account.core.model.RecoverValidationCommand
import anissia.shared.ResultWrapper

interface RecoverValidation {
    fun handle(cmd: RecoverValidationCommand): ResultWrapper<Unit>
}
