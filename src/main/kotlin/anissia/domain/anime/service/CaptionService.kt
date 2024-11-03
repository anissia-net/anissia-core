package anissia.domain.anime.service

import anissia.domain.anime.model.*
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page

interface CaptionService {
    fun getList(cmd: GetListCaptionByAnimeNoCommand, session: Session): List<CaptionItem>
    fun getList(cmd: GetMyListCaptionCommand, session: Session): Page<MyCaptionItem>
    fun getList(cmd: GetRecentListCaptionCommand): Page<CaptionRecentItem>
    fun add(cmd: AddCaptionCommand, session: Session): ResultWrapper<Unit>
    fun edit(cmd: EditCaptionCommand, session: Session): ResultWrapper<Unit>
    fun delete(cmd: DeleteCaptionCommand, session: Session): ResultWrapper<Unit>
}
