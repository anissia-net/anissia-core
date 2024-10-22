package anissia.domain.anime.service

import anissia.domain.anime.model.CaptionItem
import anissia.domain.anime.model.GetCaptionListByAnimeNoCommand
import anissia.domain.session.model.Session

interface GetCaptionListByAnimeNo {
    fun handle(cmd: GetCaptionListByAnimeNoCommand, session: Session): List<CaptionItem>
}
