package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.GetMyCaptionListCommand
import anissia.domain.anime.core.model.MyCaptionItem
import anissia.domain.session.core.model.Session
import org.springframework.data.domain.Page

interface GetMyCaptionList {
    fun handle(cmd: GetMyCaptionListCommand, session: Session): Page<MyCaptionItem>
}
