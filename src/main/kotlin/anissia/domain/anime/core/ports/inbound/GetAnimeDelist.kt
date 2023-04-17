package anissia.domain.anime.core.ports.inbound

import anissia.domain.anime.core.model.AnimeItem
import anissia.domain.session.core.model.Session
import org.springframework.data.domain.Page

interface GetAnimeDelist {
    fun handle(session: Session): Page<AnimeItem>
}
