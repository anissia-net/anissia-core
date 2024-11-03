package anissia.domain.translator.service

import anissia.domain.session.model.SessionItem
import anissia.domain.translator.command.NewApplyPollCommand
import anissia.shared.ResultWrapper

interface NewApplyPoll {
    fun handle(cmd: NewApplyPollCommand, sessionItem: SessionItem): ResultWrapper<Unit>
}
