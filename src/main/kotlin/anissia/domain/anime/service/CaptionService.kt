package anissia.domain.anime.service

import anissia.domain.account.Account
import anissia.domain.anime.command.*
import anissia.domain.anime.model.CaptionItem
import anissia.domain.anime.model.CaptionRecentItem
import anissia.domain.anime.model.MyCaptionItem
import anissia.domain.session.model.SessionItem
import org.springframework.data.domain.Page
import reactor.core.publisher.Mono

interface CaptionService {
    fun getList(cmd: GetListCaptionByAnimeNoCommand, sessionItem: SessionItem): Mono<List<CaptionItem>>
    fun getList(cmd: GetMyListCaptionCommand, sessionItem: SessionItem): Mono<Page<MyCaptionItem>>
    fun getList(cmd: GetRecentListCaptionCommand): Mono<Page<CaptionRecentItem>>
    fun add(cmd: AddCaptionCommand, sessionItem: SessionItem): Mono<String>
    fun edit(cmd: EditCaptionCommand, sessionItem: SessionItem): Mono<String>
    fun delete(cmd: DeleteCaptionCommand, sessionItem: SessionItem): Mono<String>
    fun delete(account: Account, sessionItem: SessionItem): Mono<Int>
}
