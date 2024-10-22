package anissia.domain.account.service

import anissia.domain.account.model.RecoverPasswordCommand
import anissia.shared.ResultWrapper

interface RecoverPassword {
    fun handle(cmd: RecoverPasswordCommand): ResultWrapper<Unit>
}
