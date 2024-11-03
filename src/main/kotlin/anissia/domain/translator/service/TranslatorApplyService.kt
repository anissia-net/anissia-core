package anissia.domain.translator.service

import anissia.domain.session.model.SessionItem
import anissia.domain.translator.command.AddApplyCommand
import anissia.domain.translator.command.GetApplyCommand
import anissia.domain.translator.command.GetApplyListCommand
import anissia.domain.translator.command.NewApplyPollCommand
import anissia.domain.translator.model.TranslatorApplyItem
import anissia.shared.ResultWrapper
import org.springframework.data.domain.Page
import java.time.OffsetDateTime

interface TranslatorApplyService {
    fun get(cmd: GetApplyCommand): TranslatorApplyItem
    fun getList(cmd: GetApplyListCommand): Page<TranslatorApplyItem>
    fun getApplyingCount(): Int
    fun getGrantedTime(an: Long): OffsetDateTime?
    fun isApplying(sessionItem: SessionItem): Boolean
    fun add(cmd: AddApplyCommand, sessionItem: SessionItem): ResultWrapper<Long>
    fun addPoll(cmd: NewApplyPollCommand, sessionItem: SessionItem): ResultWrapper<Unit>
}
