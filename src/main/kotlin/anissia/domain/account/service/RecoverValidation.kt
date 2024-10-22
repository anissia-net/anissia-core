package anissia.domain.account.service

import anissia.domain.account.model.RecoverValidationCommand
import anissia.shared.ResultWrapper

interface RecoverValidation {
    fun handle(cmd: RecoverValidationCommand): ResultWrapper<Unit>
}
