package anissia.domain.anime.core.service

import anissia.domain.anime.core.model.GetMyCaptionListCommand
import anissia.domain.anime.core.model.MyCaptionItem
import anissia.domain.anime.core.ports.inbound.GetMyCaptionList
import anissia.domain.anime.core.ports.outbound.AnimeCaptionRepository
import anissia.domain.session.core.model.Session
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class GetMyCaptionListService(
    private val animeCaptionRepository: AnimeCaptionRepository
): GetMyCaptionList {
    override fun handle(cmd: GetMyCaptionListCommand, session: Session): Page<MyCaptionItem> {
        cmd.validate()
        session.validateAdmin()

        return if (cmd.active == 1) {
            animeCaptionRepository.findAllWithAnimeForAdminCaptionActiveList(session.an, PageRequest.of(cmd.page, 20))
        } else {
            animeCaptionRepository.findAllWithAnimeForAdminCaptionEndList(session.an, PageRequest.of(cmd.page, 20))
        }.map { MyCaptionItem(it) }
    }
}
