package anissia.domain.activePanel.core.ports.inbound

import anissia.domain.activePanel.core.model.NewActivePanelNoticeCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface NewActivePanelNotice {
    fun handle(cmd: NewActivePanelNoticeCommand, session: Session): ResultWrapper<Unit>
}
