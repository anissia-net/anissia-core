package anissia.infrastructure.configuration


import anissia.domain.account.Account
import anissia.domain.session.model.SessionItem
import anissia.domain.session.service.JwtService
import anissia.infrastructure.common.As
import com.fasterxml.jackson.databind.ObjectMapper
import gs.shared.ErrorException
import me.saro.jwt.Jwt
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
    var objectMapper = ObjectMapper()

    // jud = json user detail
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        if ((exchange.request.headers["jud"]?.size ?: 0) > 0) {
            throw ErrorException("jud header is banned")
        }

        val jwt = exchange.request.headers["jwt"]?.get(0) ?: ""
        val ip = exchange.request.remoteAddress?.address?.hostAddress?:"0.0.0.0"

        val sessionItem = try {
            if (jwt.isBlank()) {
                SessionItem.cast(Account(), ip)
            } else {
                val jwtNode = Jwt.parseJwt(jwt) { jwtService.getKey(it.kid!!) }
                SessionItem(
                    an = (jwtNode.id!!).toLong(),
                    name = jwtNode.audience!!,
                    email = jwtNode.subject!!,
                    roles = jwtNode.claim<String>("roles")?.takeIf { it.isNotBlank() }?.split(",") ?: listOf(),
                    ip = ip
                )
            }
        } catch (e: Exception) {
            SessionItem.cast(Account(), ip)
        }

        val jud = As.encodeBase64Url(objectMapper.writeValueAsString(sessionItem))

        return chain.filter(
            exchange.mutate().request(exchange.request.mutate().header("jud", jud).build()).build());
    }
}
