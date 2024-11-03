package anissia.domain.activePanel.service

import anissia.domain.account.AccountRole
import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.command.AddTextActivePanelCommand
import anissia.domain.activePanel.command.DoCommandActivePanelCommand
import anissia.domain.activePanel.command.GetListActivePanelCommand
import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.anime.service.AnimeDocumentService
import anissia.domain.anime.service.CaptionService
import anissia.domain.session.model.SessionItem
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ActivePanelServiceImpl(
    private val activePanelRepository: ActivePanelRepository,
    private val accountRepository: AccountRepository,
    private val animeDocumentService: AnimeDocumentService,
    private val captionService: CaptionService
): ActivePanelService {
    override fun getList(cmd: GetListActivePanelCommand, sessionItem: SessionItem): Page<ActivePanelItem> {
        cmd.validate()

        return activePanelRepository.findAllByOrderByApNoDesc(PageRequest.of(cmd.page, 20))
            .run { if (cmd.mode == "admin" && sessionItem.isAdmin) this else As.filterPage(this) { it.published } }
            .map { ActivePanelItem(it) }
    }

    @Transactional
    override fun doCommand(cmd: DoCommandActivePanelCommand, sessionItem: SessionItem): ResultWrapper<Unit> {
        cmd.validate()
        sessionItem.validateAdmin()

        if (cmd.commend) { // commend
            sessionItem.validateRoot()
            when {
                // drop permission
                cmd.query.startsWith("/권한반납 ") -> {
                    val name = cmd.query.substring(cmd.query.indexOf(' ') + 1)
                    val user = accountRepository.findByName(name)
                        ?: return ResultWrapper.fail("존재하지 않는 회원입니다.")

                    if (user.isTranslator) {
                        // remove permission
                        user.roles.removeIf { it == AccountRole.TRANSLATOR }
                        accountRepository.save(user)
                        val deleteCount = captionService.delete(user, sessionItem)
                        addText(AddTextActivePanelCommand("[${user.name}]님의 자막제작자 권한이 해지되었습니다."), null)
                        addText(AddTextActivePanelCommand("[${user.name}]님의 모든 작품 ${deleteCount}개가 삭제되었습니다."), null)
                    } else {
                        return ResultWrapper.fail("${user.name}님은 자막제작자 권한을 가지고 있지 않습니다.")
                    }
                }
                cmd.query == "/검색엔진 전체갱신" -> {
                    addText(AddTextActivePanelCommand("[${sessionItem.name}]님이 검색엔진 reindex 작업을 시작했습니다."), sessionItem)
                    animeDocumentService.reset()
                    addText(AddTextActivePanelCommand("검색엔진 reindex 작업이 완료되었습니다."), sessionItem)
                }
                else -> return ResultWrapper.fail("존재하지 않는 명령입니다.")
            }

        } else { // notice
            addNotice(cmd, sessionItem)
        }
        return ResultWrapper.ok()
    }

    override fun addText(cmd: AddTextActivePanelCommand, sessionItem: SessionItem?): ResultWrapper<Unit> {
        activePanelRepository.save(ActivePanel(published = cmd.published, an = sessionItem?.an ?: sessionItem?.an ?: 0, code = "TEXT", data1 = cmd.text))
        return ResultWrapper.ok()
    }

    private fun addNotice(cmd: DoCommandActivePanelCommand, sessionItem: SessionItem) {
        activePanelRepository.save(ActivePanel(published = cmd.published, an = sessionItem.an, code = "TEXT", data1 = "《공지》 ${sessionItem.name} : ${cmd.text}"))
    }
}
