package anissia.domain.anime.service

import anissia.domain.anime.model.CaptionItem
import anissia.domain.anime.model.GetCaptionListByAnimeNoCommand
import anissia.domain.anime.model.HitAnimeCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.session.model.Session
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
