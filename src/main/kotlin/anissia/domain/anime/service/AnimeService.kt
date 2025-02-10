package anissia.domain.anime.service

import anissia.domain.anime.command.*
import anissia.domain.anime.model.AnimeItem
import anissia.domain.session.model.SessionItem
import anissia.shared.ApiResponse
import org.springframework.data.domain.Page
import reactor.core.publisher.Mono

interface AnimeService {
    fun get(cmd: GetAnimeCommand, sessionItem: SessionItem): Mono<AnimeItem>
    fun getList(cmd: GetAnimeListCommand): Mono<Page<AnimeItem>>
    fun getDelist(sessionItem: SessionItem): Mono<Page<AnimeItem>>
    fun getAutocorrect(cmd: GetAutocorrectAnimeCommand): Mono<List<String>>
    fun add(cmd: NewAnimeCommand, sessionItem: SessionItem): Mono<Long>
    fun edit(cmd: EditAnimeCommand, sessionItem: SessionItem): Mono<Long>
    fun delete(cmd: DeleteAnimeCommand, sessionItem: SessionItem): Mono<String>
    fun recover(cmd: RecoverAnimeCommand, sessionItem: SessionItem): Mono<Long>
}
