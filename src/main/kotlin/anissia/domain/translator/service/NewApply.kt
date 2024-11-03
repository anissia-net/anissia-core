package anissia.domain.translator.service

import anissia.domain.session.model.SessionItem
import anissia.domain.translator.command.NewApplyCommand
import anissia.shared.ResultWrapper

interface NewApply {
    fun handle(cmd: NewApplyCommand, sessionItem: SessionItem): ResultWrapper<Long>
}
