package anissia.domain.anime.core.service

import anissia.domain.anime.core.model.CaptionItem
import anissia.domain.anime.core.model.GetCaptionListByAnimeNoCommand
import anissia.domain.anime.core.model.HitAnimeCommand
import anissia.domain.anime.core.ports.inbound.GetCaptionListByAnimeNo
import anissia.domain.anime.core.ports.inbound.HitAnime
import anissia.domain.anime.core.ports.outbound.AnimeCaptionRepository
import anissia.domain.session.core.model.Session
import org.springframework.stereotype.Service

@Service
class GetCaptionListByAnimeNoService(
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val hitAnime: HitAnime
): GetCaptionListByAnimeNo {
    override fun handle(cmd: GetCaptionListByAnimeNoCommand, session: Session): List<CaptionItem> {
        cmd.validate()
        return animeCaptionRepository.findAllWithAccountByAnimeNoOrderByUpdDtDesc(cmd.animeNo).map { CaptionItem(it) }
            .also { hitAnime.handle(HitAnimeCommand(cmd.animeNo), session) }
    }
}
