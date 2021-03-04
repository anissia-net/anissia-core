package anissia.controller

import anissia.dto.ResultData
import anissia.dto.request.TranslatorApplyRequest
import anissia.services.TranslatorService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/translator")
class TranslatorController(
    private val translatorService: TranslatorService
) {
    @GetMapping("/apply/list/{page:[\\d]+}")
    fun getApplyList(@PathVariable page: Int) = translatorService.getApplyList(page)

    @GetMapping("/apply/applyNo/{applyNo:[\\d]+}")
    fun getApply(@PathVariable applyNo: Long) = translatorService.getApply(applyNo)

    @PostMapping("/apply")
    fun createApply(@RequestBody @Valid translatorApplyRequest: TranslatorApplyRequest) =
            translatorService.createApply(translatorApplyRequest)
}