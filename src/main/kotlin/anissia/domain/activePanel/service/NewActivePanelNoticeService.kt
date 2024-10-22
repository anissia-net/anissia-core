package anissia.domain.activePanel.service


import anissia.domain.account.model.AccountRole
import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.model.NewActivePanelNoticeCommand
import anissia.domain.activePanel.model.NewActivePanelTextCommand
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NewActivePanelNoticeService(
    private val activePanelRepository: ActivePanelRepository,
    private val accountRepository: AccountRepository,
    private val newActivePanelText: NewActivePanelText
): NewActivePanelNotice {

    @Transactional
    override fun handle(cmd: NewActivePanelNoticeCommand, session: Session): ResultWrapper<Unit> {
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
                        //adminService.deleteCaption(user)
                        newActivePanelText.handle(NewActivePanelTextCommand("[${user.name}]님의 자막제작자 권한이 해지되었습니다."), null)
                    } else {
                        return ResultWrapper.fail("${user.name}님은 자막제작자 권한을 가지고 있지 않습니다.")
                    }
                }
                else -> return ResultWrapper.fail("존재하지 않는 명령입니다.")
            }

        } else { // just text
            activePanelRepository.save(ActivePanel(published = cmd.published, an = session.an, code = "TEXT", data1 = "《공지》 ${session.name} : ${cmd.text}"))
        }
        return ResultWrapper.ok()
    }
}
