package anissia.domain.anime.service

import anissia.domain.anime.model.AnimeItem
import anissia.domain.session.model.Session
import org.springframework.data.domain.Page

interface GetAnimeDelist {
    fun handle(session: Session): Page<AnimeItem>
}
