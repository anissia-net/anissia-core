package anissia.services

import anissia.misc.As
import anissia.rdb.domain.ActivePanel
import anissia.rdb.dto.ActivePanelDto
import anissia.rdb.dto.ResultStatus
import anissia.rdb.repository.ActivePanelRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ActivePanelService(
    private val activePanelRepository: ActivePanelRepository,
    private val sessionService: SessionService
) {
    val user get() = sessionService.session
    val isManager get() = sessionService.isManager

    fun getList(page: Int): Page<ActivePanelDto> =
        activePanelRepository.findAllByOrderByApNoDesc(PageRequest.of(page, 20))
            .run { if (isManager) this else As.filterPage(this) { it.published } }
            .map { ActivePanelDto(it) }

    fun saveText(text: String, published: Boolean = true, an: Long? = null) =
        activePanelRepository.save(ActivePanel(published = published, an = an ?: user?.an ?: 0, code = "TEXT", data1 = text))

    fun saveNotice(text: String, published: Boolean = false): ResultStatus =
        activePanelRepository.save(ActivePanel(published = published, an = user?.an ?: 0, code = "TEXT", data1 = "《공지》 ${user?.name} : $text"))
            .run { ResultStatus("OK") }
}