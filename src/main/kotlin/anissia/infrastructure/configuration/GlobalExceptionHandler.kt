package anissia.infrastructure.configuration

import anissia.infrastructure.common.logger
import anissia.infrastructure.common.toJsonBytes
import anissia.shared.ApiErrorException
import anissia.shared.ApiFailException
import anissia.shared.ApiResponse
import org.springframework.core.NestedRuntimeException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.server.*
import reactor.core.publisher.Mono


@Order(-2)
@Component
class GlobalExceptionHandler : WebExceptionHandler {

    private val log = logger<GlobalExceptionHandler>()

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> =
        when (ex) {
            is Error -> exchange.response.write(ApiResponse.error("알수없는 오류 입니다."))

            is ServerWebInputException -> exchange.response.write(ApiResponse.error("입력값이 잘못되었습니다."))

            is IllegalArgumentException, is ApiFailException -> exchange.response.write(ApiResponse.error(ex.message ?: ex.javaClass.simpleName))

            is MethodNotAllowedException -> exchange.response.write(ApiResponse.error("METHOD_NOT_ALLOWED"), HttpStatus.METHOD_NOT_ALLOWED)
                .also { log.info(exchange.request.let { "MNA ${it.method} ${it.uri} ${it.remoteAddress?.address?.hostAddress?:"0.0.0.0"}" }) }

            is ResponseStatusException -> {
                exchange.response.write(ApiResponse.error(ex.message), ex.statusCode)
                    .also { log.info(exchange.request.let { "RSE ${it.method} ${it.uri} ${ex.message} ${it.remoteAddress?.address?.hostAddress?:"0.0.0.0"}" }) }
            }

            is MethodArgumentNotValidException -> {
                exchange.response.write(try {
                    ApiResponse.error(ex.bindingResult.allErrors[0].defaultMessage ?: "입력값이 잘못되었습니다.")
                } catch (e: Exception) {
                    ApiResponse.error("입력값이 잘못되었습니다.")
                })
            }

            is SecurityException -> {
                exchange.response.write(ApiResponse.error(ex.message), HttpStatus.UNAUTHORIZED)
                    .also { log(ex, exchange) }
            }

            is NestedRuntimeException, is ApiErrorException -> {
                exchange.response.write(ApiResponse.error(ex.message ?: ex.javaClass.simpleName))
                    .also { log(ex, exchange) }
            }

            else -> {
                exchange.response.write(ApiResponse.error("unknown error"))
                    .also { log(ex, exchange) }
            }
        }

    fun ServerHttpResponse.write(apiResponse: ApiResponse<String>, status: HttpStatusCode = HttpStatus.OK): Mono<Void> =
        also { it.statusCode = status }
            .writeWith(Mono.just(bufferFactory().wrap(apiResponse.toJsonBytes)))

    private fun log(throwable: Throwable, exchange: ServerWebExchange) {
        val message = exchange.request.let { "${throwable.message}\n${it.method} ${it.uri}" }
        log.error(message, throwable)
    }
}
