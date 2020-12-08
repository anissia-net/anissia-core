package anissia.configruration

import org.springframework.web.bind.annotation.ControllerAdvice

@ControllerAdvice
class GlobalExceptionHandler {

//    /**
//     * it is advice of invalid RequestBody
//     */
//    @ExceptionHandler(MethodArgumentNotValidException::class)
//    fun methodArgumentNotValidException(exception: MethodArgumentNotValidException, request: WebRequest)
//            = exception.bindingResult.allErrors?.firstOrNull()?.defaultMessage?.takeIf { it.isNotBlank() }
//            .run {
//                ResponseEntity.status(HttpStatus.OK).body(BasicResultModel<String>("ERROR", this ?: "비정상적인 파라미터 호출입니다."))
//            }

}
