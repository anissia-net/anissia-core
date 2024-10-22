package anissia.domain.anime.service

import anissia.domain.anime.model.GetMyCaptionListCommand
import anissia.domain.anime.model.MyCaptionItem
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.session.model.Session
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
