package anissia.infrastructure.configuration


import anissia.domain.session.service.JwtService
import anissia.infrastructure.common.encodeBase64Url
import anissia.infrastructure.common.toJson
import anissia.shared.ApiErrorException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

// it will change to jwt, now it is not jwt
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class JwtDecoderFilter(
    private val jwtService: JwtService
): WebFilter {

    // jud = json user detail
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> =
        Mono.fromCallable { exchange.request }
            .flatMap { request ->
                if ((request.headers["jud"]?.size ?: 0) > 0) {
                    // 사용자가 직접 jud를 지정한 경우 허용하지 않는다.
                    Mono.error(ApiErrorException("jud header is banned"))
                } else {
                    val jwt = request.headers["jwt"]?.get(0) ?: ""
                    val ip = request.remoteAddress?.address?.hostAddress?:"0.0.0.0"
                    jwtService.toSessionItem(jwt, ip)
                        .map { sessionItem -> sessionItem.toJson.encodeBase64Url }
                        .map { jud -> request.mutate().header("jud", jud).build() }
                        .flatMap { header -> chain.filter(exchange.mutate().request(header).build()) }
                }
            }
}
