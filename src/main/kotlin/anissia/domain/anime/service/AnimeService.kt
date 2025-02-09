package anissia.domain.anime.service

import anissia.domain.anime.command.*
import anissia.domain.anime.model.AnimeItem
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page

interface AnimeService {
    fun get(cmd: GetAnimeCommand, sessionItem: SessionItem): AnimeItem
    fun getList(cmd: GetAnimeListCommand): Page<AnimeItem>
    fun getDelist(sessionItem: SessionItem): Page<AnimeItem>
    fun getAutocorrect(cmd: GetAutocorrectAnimeCommand): List<String>
    fun add(cmd: NewAnimeCommand, sessionItem: SessionItem): Mono<ApiResponse<Long>
    fun edit(cmd: EditAnimeCommand, sessionItem: SessionItem): Mono<ApiResponse<Long>
    fun delete(cmd: DeleteAnimeCommand, sessionItem: SessionItem): Mono<String>
    fun recover(cmd: RecoverAnimeCommand, sessionItem: SessionItem): Mono<ApiResponse<Long>
}
