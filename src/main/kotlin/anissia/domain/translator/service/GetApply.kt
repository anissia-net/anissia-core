package anissia.domain.translator.service

import anissia.domain.translator.core.model.GetApplyCommand
import anissia.domain.translator.core.model.TranslatorApplyItem

interface GetApply {
    fun handle(cmd: GetApplyCommand): TranslatorApplyItem
}
