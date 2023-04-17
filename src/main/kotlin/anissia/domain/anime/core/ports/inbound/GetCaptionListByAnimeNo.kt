package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.CaptionItem
import anissia.domain.anime.core.model.GetCaptionListByAnimeNoCommand
import anissia.domain.session.core.model.Session

interface GetCaptionListByAnimeNo {
    fun handle(cmd: GetCaptionListByAnimeNoCommand, session: Session): List<CaptionItem>
}
