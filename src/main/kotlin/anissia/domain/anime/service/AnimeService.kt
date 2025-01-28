package anissia.domain.anime.service

import anissia.domain.anime.command.*
import anissia.domain.anime.model.AnimeItem
import anissia.domain.session.model.SessionItem
import anissia.shared.ApiResponse
import org.springframework.data.domain.Page

interface AnimeService {
    fun get(cmd: GetAnimeCommand, sessionItem: SessionItem): AnimeItem
    fun getList(cmd: GetAnimeListCommand): Page<AnimeItem>
    fun getDelist(sessionItem: SessionItem): Page<AnimeItem>
    fun getAutocorrect(cmd: GetAutocorrectAnimeCommand): List<String>
    fun add(cmd: NewAnimeCommand, sessionItem: SessionItem): ApiResponse<Long>
    fun edit(cmd: EditAnimeCommand, sessionItem: SessionItem): ApiResponse<Long>
    fun delete(cmd: DeleteAnimeCommand, sessionItem: SessionItem): ApiResponse<Void>
    fun recover(cmd: RecoverAnimeCommand, sessionItem: SessionItem): ApiResponse<Long>
}
