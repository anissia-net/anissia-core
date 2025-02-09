package anissia.infrastructure.configuration

import anissia.infrastructure.common.logger
import anissia.shared.ApiException
import anissia.shared.ApiFailException
import anissia.shared.ApiResponse
import org.springframework.core.NestedRuntimeException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException

@ControllerAdvice
class GlobalExceptionHandler {

    private val log = logger<GlobalExceptionHandler>()

    @ExceptionHandler(Error::class)
    fun handleNotImplementedError(ex: Error, exchange: ServerWebExchange): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.error<Unit>("알수없는 오류 입니다."))
            .also { log(ex, exchange) }
    }

    @ExceptionHandler(Exception::class)
    fun other(exception: Exception, exchange: ServerWebExchange): ResponseEntity<ApiResponse<Unit>> {
        return when (exception) {
            is ServerWebInputException -> {
                ResponseEntity.status(HttpStatus.OK).body(ApiResponse.error<Unit>("입력값이 잘못되었습니다."))
            }
            is IllegalArgumentException, is ApiFailException -> {
                ResponseEntity.status(HttpStatus.OK).body(ApiResponse.error<Unit>(exception.message ?: exception.javaClass.simpleName))
            }
            is MethodNotAllowedException -> {
                ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ApiResponse.error<Unit>("METHOD_NOT_ALLOWED"))
                    .also { log.info(exchange.request.let { "MNA ${it.method} ${it.uri} ${it.remoteAddress?.address?.hostAddress?:"0.0.0.0"}" }) }
            }
            is ResponseStatusException -> {
                val code = exception.statusCode.value()
                ResponseEntity.status(code).body(ApiResponse.error<Unit>(exception.message))
                    .also { log.info(exchange.request.let { "RSE ${it.method} ${it.uri} ${exception.message} ${it.remoteAddress?.address?.hostAddress?:"0.0.0.0"}" }) }
            }
            is MethodArgumentNotValidException -> {
                val error = try {
                    ApiResponse.error<Unit>(exception.bindingResult.allErrors[0].defaultMessage ?: "입력값이 잘못되었습니다.")
                } catch (e: Exception) {
                    ApiResponse.error<Unit>("입력값이 잘못되었습니다.")
                }
                ResponseEntity.status(HttpStatus.OK).body(error)
            }
            is SecurityException -> {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error<Unit>(exception.message))
                    .also { log(exception, exchange) }
            }
            is NestedRuntimeException, is ApiException -> {
                ResponseEntity.status(HttpStatus.OK).body(ApiResponse.error<Unit>(exception.message ?: exception.javaClass.simpleName))
                    .also { log(exception, exchange) }
            }
            else -> {
                ResponseEntity.status(HttpStatus.OK).body(ApiResponse.error<Unit>("unknown error"))
                    .also { log(exception, exchange) }
            }
        }
    }

    private fun log(throwable: Throwable, exchange: ServerWebExchange) {
        val message = exchange.request.let { "${throwable.message}\n${it.method} ${it.uri}" }
        log.error(message, throwable)
    }
}
