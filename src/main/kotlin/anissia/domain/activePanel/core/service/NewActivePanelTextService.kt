package anissia.domain.activePanel.core.service

import anissia.domain.account.core.ports.outbound.AccountRepository
import anissia.domain.activePanel.core.ActivePanel
import anissia.domain.activePanel.core.model.NewActivePanelTextCommand
import anissia.domain.activePanel.core.ports.inbound.NewActivePanelText
import anissia.domain.activePanel.core.ports.outbound.ActivePanelRepository
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper
import org.springframework.stereotype.Service

@Service
class NewActivePanelTextService(
    private val activePanelRepository: ActivePanelRepository,
    private val accountRepository: AccountRepository,
): NewActivePanelText {
    override fun handle(cmd: NewActivePanelTextCommand, session: Session?): ResultWrapper<Unit> {
        activePanelRepository.save(ActivePanel(published = cmd.published, an = session?.an ?: session?.an ?: 0, code = "TEXT", data1 = cmd.text))
        return ResultWrapper.ok()
    }
}
