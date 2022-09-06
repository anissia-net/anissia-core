package anissia.controller

import anissia.dto.request.TranslatorApplyPollRequest
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

    @GetMapping("/apply/{applyNo:[\\d]+}")
    fun getApply(@PathVariable applyNo: Long) = translatorService.getApply(applyNo)

    @PostMapping("/apply")
    fun createApply(@RequestBody @Valid translatorApplyRequest: TranslatorApplyRequest) =
            translatorService.createApply(translatorApplyRequest)

    @PostMapping("/apply/{applyNo:[\\d]+}/poll")
    fun createApplyPoll(@PathVariable applyNo: Long, @RequestBody @Valid translatorApplyPollRequest: TranslatorApplyPollRequest) =
            translatorService.createApplyPoll(applyNo, translatorApplyPollRequest)
}