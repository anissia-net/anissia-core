package anissia.domain.activePanel.service

import anissia.domain.account.AccountRole
import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.command.AddTextActivePanelCommand
import anissia.domain.activePanel.command.DoCommandActivePanelCommand
import anissia.domain.anime.service.AnimeDocumentService
import anissia.domain.anime.service.CaptionService
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ActivePanelCommandServiceImpl(
    private val accountRepository: AccountRepository,
    private val animeDocumentService: AnimeDocumentService,
    private val activePanelLogService: ActivePanelLogService,
    private val captionService: CaptionService,
) : ActivePanelCommandService {
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
                        sessionItem.addText("[${user.name}]님의 자막제작자 권한이 해지되었습니다.")
                        sessionItem.addText("[${user.name}]님의 모든 작품 ${deleteCount}개가 삭제되었습니다.")
                    } else {
                        return ResultWrapper.fail("${user.name}님은 자막제작자 권한을 가지고 있지 않습니다.")
                    }
                }
                cmd.query == "/검색엔진 전체갱신" -> {
                    sessionItem.addText("[${sessionItem.name}]님이 검색엔진 reindex 작업을 시작했습니다.")
                    animeDocumentService.reset(false)
                    sessionItem.addText("검색엔진 reindex 작업이 완료되었습니다.")
                }
                cmd.query == "/검색엔진 초기화" -> {
                    sessionItem.addText("[${sessionItem.name}]님이 검색엔진 초기화 작업을 시작했습니다.")
                    animeDocumentService.reset(true)
                    sessionItem.addText("검색엔진 초기화 작업이 완료되었습니다.")
                }
                else -> return ResultWrapper.fail("존재하지 않는 명령입니다.")
            }

        } else { // notice
            return sessionItem.addNotice(cmd)
        }
        return ResultWrapper.ok()
    }

    private fun SessionItem.addText(text: String) =
        activePanelLogService.addText(AddTextActivePanelCommand(text), this)

    private fun SessionItem.addNotice(cmd: DoCommandActivePanelCommand) =
        activePanelLogService.addNotice(cmd, this)
}
