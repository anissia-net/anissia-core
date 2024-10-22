package anissia.domain.activePanel.service

import anissia.domain.activePanel.model.NewActivePanelTextCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface NewActivePanelText {
    fun handle(cmd: NewActivePanelTextCommand, session: Session?): ResultWrapper<Unit>
}
