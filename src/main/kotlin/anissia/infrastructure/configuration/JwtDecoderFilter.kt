package anissia.infrastructure.configuration


import anissia.domain.account.Account
import anissia.domain.session.model.SessionItem
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
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<String> =
        Mono.fromCallable { exchange.request }
            .filter { (it.headers["jud"]?.size ?: 0) > 0 }
            .switchIfEmpty(Mono.error(ApiErrorException("jud header is banned")))
            .map { request ->
                val jwt = request.headers["jwt"]?.get(0) ?: ""
                val ip = request.remoteAddress?.address?.hostAddress?:"0.0.0.0"
                var sessionItem: SessionItem? = null

                try {
                    if (jwt.isNotBlank()) {
                        val key = jwtService.getKey(jwtService.alg().toJwtHeader(jwt).kid!!)
                        val claims = jwtService.alg().toJwtClaims(jwt, key)
                        val id = (claims.id!!).toLong()
                        val roles = claims.claim<String>("roles")?.takeIf { it.isNotBlank() }?.split(",") ?: listOf()
                        claims.assert()
                        sessionItem = SessionItem(
                            an = id,
                            name = claims.audience!!,
                            email = claims.subject!!,
                            roles = roles,
                            ip = ip
                        )
                    }
                } catch (_: Exception) {}

                request.mutate().header("jud", (sessionItem?: SessionItem.cast(Account(), ip)).toJson.encodeBase64Url).build()
            }
            .flatMap { header -> chain.filter(exchange.mutate().request(header).build()) }
}
