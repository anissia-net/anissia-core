package anissia.domain.activePanel.service

import anissia.domain.activePanel.model.NewActivePanelNoticeCommand
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper

interface NewActivePanelNotice {
    fun handle(cmd: NewActivePanelNoticeCommand, session: Session): ResultWrapper<Unit>
}
