package anissia.domain.anime.service

import anissia.domain.account.Account
import anissia.domain.anime.command.*
import anissia.domain.anime.model.*
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page

interface CaptionService {
    fun getList(cmd: GetListCaptionByAnimeNoCommand, sessionItem: SessionItem): List<CaptionItem>
    fun getList(cmd: GetMyListCaptionCommand, sessionItem: SessionItem): Page<MyCaptionItem>
    fun getList(cmd: GetRecentListCaptionCommand): Page<CaptionRecentItem>
    fun add(cmd: AddCaptionCommand, sessionItem: SessionItem): ResultWrapper<Unit>
    fun edit(cmd: EditCaptionCommand, sessionItem: SessionItem): ResultWrapper<Unit>
    fun delete(cmd: DeleteCaptionCommand, sessionItem: SessionItem): ResultWrapper<Unit>
    fun delete(account: Account, sessionItem: SessionItem): Int
}
