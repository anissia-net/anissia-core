package anissia.services

import anissia.rdb.domain.ActivePanel
import anissia.rdb.dto.ActivePanelDto
import anissia.rdb.repository.ActivePanelRepository
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ActivePanelService(
    private val activePanelRepository: ActivePanelRepository,
    private val sessionService: SessionService
) {
    fun getList(page: Int): Page<ActivePanelDto> =
        activePanelRepository.findAllByOrderByApNoDesc(PageRequest.of(page, 20))
            .apply { if (!sessionService.isManager) { this.filter { it.published } } }
            .map { ActivePanelDto(it) }

    fun saveText(text: String, published: Boolean = true, an: Long? = null) =
        activePanelRepository.save(ActivePanel(published = published, an = an ?: sessionService.session?.an ?: 0, code = "TEXT", data1 = text))
}