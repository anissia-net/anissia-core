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
            .filter { (it.headers["jud"]?.size ?: 0) > 0 }
            .switchIfEmpty(Mono.error(ApiErrorException("jud header is banned")))
            .map { request ->
                val jwt = request.headers["jwt"]?.get(0) ?: ""
                val ip = request.remoteAddress?.address?.hostAddress?:"0.0.0.0"
                request.mutate().header("jud", jwtService.toSessionItem(jwt, ip).toJson.encodeBase64Url).build()
            }
            .flatMap { header -> chain.filter(exchange.mutate().request(header).build()) }
}
