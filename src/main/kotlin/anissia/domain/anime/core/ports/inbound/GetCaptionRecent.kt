package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.CaptionRecentItem
import anissia.domain.anime.core.model.GetCaptionRecentCommand
import org.springframework.data.domain.Page

interface GetCaptionRecent {
    fun handle(cmd: GetCaptionRecentCommand): Page<CaptionRecentItem>
}
