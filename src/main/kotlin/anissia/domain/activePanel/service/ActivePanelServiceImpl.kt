package anissia.domain.activePanel.service

import anissia.domain.account.AccountRole
import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.model.ActivePanelItem
import anissia.domain.activePanel.model.AddTextActivePanelCommand
import anissia.domain.activePanel.model.DoCommandActivePanelCommand
import anissia.domain.activePanel.model.GetListActivePanelCommand
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.anime.service.AnimeDocumentService
import anissia.domain.anime.service.CaptionService
import anissia.domain.session.model.Session
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
    override fun getList(cmd: GetListActivePanelCommand, session: Session): Page<ActivePanelItem> {
        cmd.validate()

        return activePanelRepository.findAllByOrderByApNoDesc(PageRequest.of(cmd.page, 20))
            .run { if (cmd.mode == "admin" && session.isAdmin) this else As.filterPage(this) { it.published } }
            .map { ActivePanelItem(it) }
    }

    @Transactional
    override fun doCommand(cmd: DoCommandActivePanelCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateAdmin()

        if (cmd.commend) { // commend
            session.validateRoot()
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
                        val deleteCount = captionService.delete(user, session)
                        addText(AddTextActivePanelCommand("[${user.name}]님의 자막제작자 권한이 해지되었습니다."), null)
                        addText(AddTextActivePanelCommand("[${user.name}]님의 모든 작품 ${deleteCount}개가 삭제되었습니다."), null)
                    } else {
                        return ResultWrapper.fail("${user.name}님은 자막제작자 권한을 가지고 있지 않습니다.")
                    }
                }
                cmd.query == "/검색엔진 전체갱신" -> {
                    addText(AddTextActivePanelCommand("[${session.name}]님이 검색엔진 reindex 작업을 시작했습니다."), session)
                    animeDocumentService.reset()
                    addText(AddTextActivePanelCommand("검색엔진 reindex 작업이 완료되었습니다."), session)
                }
                else -> return ResultWrapper.fail("존재하지 않는 명령입니다.")
            }

        } else { // notice
            addNotice(cmd, session)
        }
        return ResultWrapper.ok()
    }

    override fun addText(cmd: AddTextActivePanelCommand, session: Session?): ResultWrapper<Unit> {
        activePanelRepository.save(ActivePanel(published = cmd.published, an = session?.an ?: session?.an ?: 0, code = "TEXT", data1 = cmd.text))
        return ResultWrapper.ok()
    }

    private fun addNotice(cmd: DoCommandActivePanelCommand, session: Session) {
        activePanelRepository.save(ActivePanel(published = cmd.published, an = session.an, code = "TEXT", data1 = "《공지》 ${session.name} : ${cmd.text}"))
    }
}
