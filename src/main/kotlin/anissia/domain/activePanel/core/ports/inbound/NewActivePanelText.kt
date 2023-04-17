package anissia.domain.activePanel.core.ports.inbound

import anissia.domain.activePanel.core.model.NewActivePanelTextCommand
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper

interface NewActivePanelText {
    fun handle(cmd: NewActivePanelTextCommand, session: Session?): ResultWrapper<Unit>
}
