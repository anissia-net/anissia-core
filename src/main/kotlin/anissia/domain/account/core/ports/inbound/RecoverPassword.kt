package anissia.domain.account.core.ports.inbound

import anissia.domain.account.core.model.RecoverPasswordCommand
import anissia.shared.ResultWrapper

interface RecoverPassword {
    fun handle(cmd: RecoverPasswordCommand): ResultWrapper<Unit>
}
