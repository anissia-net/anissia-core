package anissia.domain.anime.service

import anissia.domain.anime.model.GetMyCaptionListCommand
import anissia.domain.anime.model.MyCaptionItem
import anissia.domain.session.model.Session
import org.springframework.data.domain.Page

interface GetMyCaptionList {
    fun handle(cmd: GetMyCaptionListCommand, session: Session): Page<MyCaptionItem>
}
