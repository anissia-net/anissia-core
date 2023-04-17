package anissia.domain.translator.core.ports.inbound

import anissia.domain.translator.core.model.GetApplyListCommand
import anissia.domain.translator.core.model.TranslatorApplyItem
import org.springframework.data.domain.Page

interface GetApplyList {
    fun handle(cmd: GetApplyListCommand): Page<TranslatorApplyItem>
}
