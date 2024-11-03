package anissia.domain.translator.service

import anissia.domain.translator.command.GetApplyListCommand
import anissia.domain.translator.model.TranslatorApplyItem
import org.springframework.data.domain.Page

interface GetApplyList {
    fun handle(cmd: GetApplyListCommand): Page<TranslatorApplyItem>
}
