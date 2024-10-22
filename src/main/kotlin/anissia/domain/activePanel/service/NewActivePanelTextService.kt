package anissia.domain.activePanel.service

import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.model.NewActivePanelTextCommand
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.session.model.Session
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
