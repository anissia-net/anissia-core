package anissia.domain.translator.service

import anissia.domain.session.model.SessionItem
import anissia.domain.translator.command.AddApplyCommand
import anissia.domain.translator.command.GetApplyCommand
import anissia.domain.translator.command.GetApplyListCommand
import anissia.domain.translator.command.NewApplyPollCommand
import anissia.domain.translator.model.TranslatorApplyItem
import org.springframework.data.domain.Page
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

interface TranslatorApplyService {
    fun get(cmd: GetApplyCommand): Mono<TranslatorApplyItem>
    fun getList(cmd: GetApplyListCommand): Mono<Page<TranslatorApplyItem>>
    fun getApplyingCount(): Mono<Int>
    fun getGrantedTime(an: Long): Mono<OffsetDateTime>
    fun isApplying(sessionItem: SessionItem): Mono<Boolean>
    fun add(cmd: AddApplyCommand, sessionItem: SessionItem): Mono<Long>
    fun addPoll(cmd: NewApplyPollCommand, sessionItem: SessionItem): Mono<String>
}
