package anissia.domain.translator.service

import anissia.domain.session.model.Session
import anissia.domain.translator.model.NewApplyCommand
import anissia.shared.ResultWrapper

interface NewApply {
    fun handle(cmd: NewApplyCommand, session: Session): ResultWrapper<Long>
}
