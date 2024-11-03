package anissia.domain.anime.service

import anissia.domain.anime.command.*
import anissia.domain.anime.model.*
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page

interface AnimeService {
    fun get(cmd: GetAnimeCommand, session: Session): AnimeItem
    fun getList(cmd: GetAnimeListCommand): Page<AnimeItem>
    fun getDelist(session: Session): Page<AnimeItem>
    fun getAutocorrect(cmd: GetAutocorrectAnimeCommand): List<String>
    fun add(cmd: NewAnimeCommand, session: Session): ResultWrapper<Long>
    fun edit(cmd: EditAnimeCommand, session: Session): ResultWrapper<Long>
    fun delete(cmd: DeleteAnimeCommand, session: Session): ResultWrapper<Unit>
    fun recover(cmd: RecoverAnimeCommand, session: Session): ResultWrapper<Long>
}
