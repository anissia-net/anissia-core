package anissia.domain.translator.service

import anissia.domain.translator.model.GetApplyCommand
import anissia.domain.translator.model.TranslatorApplyItem

interface GetApply {
    fun handle(cmd: GetApplyCommand): TranslatorApplyItem
}
