package anissia.services

import anissia.dto.ActivePanelDto
import anissia.dto.ResultStatus
import anissia.dto.request.ActivePanelNoticeRequest
import anissia.misc.As
import anissia.rdb.entity.AccountRole
import anissia.rdb.entity.ActivePanel
import anissia.rdb.repository.AccountRepository
import anissia.rdb.repository.ActivePanelRepository
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ActivePanelService(
    private val activePanelRepository: ActivePanelRepository,
    @Lazy private val adminService: AdminService,
    private val accountRepository: AccountRepository,
    private val sessionService: SessionService
) {
    val user get() = sessionService.session
    val isManager get() = sessionService.isManager

    fun save(activePanel: ActivePanel) = activePanelRepository.save(activePanel)

    fun getList(isAdminMode: Boolean, page: Int): Page<ActivePanelDto> =
        activePanelRepository.findAllByOrderByApNoDesc(PageRequest.of(page, 20))
            .run { if (isAdminMode && isManager) this else As.filterPage(this) { it.published } }
            .map { ActivePanelDto(it) }

    fun saveText(text: String, published: Boolean = true, an: Long? = null) =
        activePanelRepository.save(ActivePanel(published = published, an = an ?: user?.an ?: 0, code = "TEXT", data1 = text))

    fun saveNotice(apnr: ActivePanelNoticeRequest): ResultStatus {
        if (apnr.commend) { // commend
            if (!user!!.isRoot()) {
                return ResultStatus("FAIL", "권한이 없습니다.")
            }
            when {
                // drop permission
                apnr.query.startsWith("/권한반납 ") -> {
                    val name = apnr.query.substring(apnr.query.indexOf(' ') + 1)
                    val user = accountRepository.findByName(name)
                        ?: return ResultStatus("FAIL", "존재하지 않는 회원입니다.")

                    if (user.roles.any { it == AccountRole.TRANSLATOR }) {
                        // remove permission
                        user.roles.removeIf { it == AccountRole.TRANSLATOR }
                        accountRepository.save(user)
                        adminService.deleteCaption(user)
                        saveText("[${user.name}]님의 자막제작자 권한이 해지되었습니다.")
                    } else {
                        return ResultStatus("FAIL", "${user.name}님은 자막제작자 권한을 가지고 있지 않습니다.")
                    }
                }
                else -> return ResultStatus("FAIL", "존재하지 않는 명령입니다.")
            }

        } else { // just text
            activePanelRepository.save(ActivePanel(published = apnr.published, an = user?.an ?: 0, code = "TEXT", data1 = "《공지》 ${user?.name} : ${apnr.text}"))
        }
        return ResultStatus("OK")
    }

}
