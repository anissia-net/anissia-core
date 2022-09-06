package anissia.configruration

import anissia.dto.ResultData
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {

    /**
     * it is advice of invalid RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(exception: MethodArgumentNotValidException, request: WebRequest)
            = exception.bindingResult.allErrors.firstOrNull()?.defaultMessage?.takeIf { it.isNotBlank() }
            .run {
                ResponseEntity.status(HttpStatus.OK).body(ResultData<String>("ERROR", this ?: "비정상적인 파라미터 호출입니다."))
            }

}
