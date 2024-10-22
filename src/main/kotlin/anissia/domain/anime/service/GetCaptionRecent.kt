package anissia.domain.anime.service

import anissia.domain.anime.model.CaptionRecentItem
import anissia.domain.anime.model.GetCaptionRecentCommand
import org.springframework.data.domain.Page

interface GetCaptionRecent {
    fun handle(cmd: GetCaptionRecentCommand): Page<CaptionRecentItem>
}
