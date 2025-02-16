package anissia.domain.session.service

import anissia.domain.account.Account
import anissia.domain.account.repository.AccountRepository
import anissia.domain.session.JwtKeyPair
import anissia.domain.session.LoginPass
import anissia.domain.session.LoginToken
import anissia.domain.session.command.GetJwtAuthInfoCommand
import anissia.domain.session.model.JwtAuthInfoItem
import anissia.domain.session.model.JwtKeyItem
import anissia.domain.session.model.SessionItem
import anissia.domain.session.repository.JwtKeyPairRepository
import anissia.domain.session.repository.LoginFailRepository
import anissia.domain.session.repository.LoginPassRepository
import anissia.domain.session.repository.LoginTokenRepository
import me.saro.jwt.alg.es.JwtEs256
import me.saro.jwt.core.Jwt
import me.saro.jwt.core.JwtClaims
import me.saro.jwt.core.JwtKey
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

@Service
class JwtServiceImpl(
    private val jwtKeyPairRepository: JwtKeyPairRepository,
    private val accountRepository: AccountRepository,
    private val loginTokenRepository: LoginTokenRepository,
    private val loginPassRepository: LoginPassRepository,
    private val loginFailRepository: LoginFailRepository,
): JwtService {
    private val keyStore: Deque<JwtKeyItem> = ConcurrentLinkedDeque()
    private val es256: JwtEs256 = Jwt.es256()
    private val keyStoreSize = 24

    override fun registerNewJwtKey(): Mono<String> =
        Mono.fromCallable { jwtKeyPairRepository.save(JwtKeyPair(System.currentTimeMillis(), es256.newRandomJwtKey().stringify)); "" }

    override fun renewKeyStore(): Mono<String> =
        Flux.just(*jwtKeyPairRepository.findAllByOrderByKidDesc(PageRequest.of(0, keyStoreSize)).toTypedArray())
            .map { JwtKeyItem(it.kid.toString(), es256.toJwtKeyByStringify(it.data)) }
            .filter { item -> keyStore.none { it.kid == item.kid } }
            .collectList()
            .map {
                it.reversed().forEach { item -> keyStore.addFirst(item) }
                repeat((keyStore.size - keyStoreSize).coerceAtLeast(0)) { keyStore.removeLast() }
                ""
            }

    override fun toSessionItem(jwt: String, ip: String): Mono<SessionItem> =
        Mono.fromCallable {
            if (jwt.isNotBlank()) {
                try {
                    val key = findKey(es256.toJwtHeader(jwt).kid!!)
                    val claims = es256.toJwtClaims(jwt, key)
                    val id = (claims.id!!).toLong()
                    val roles = claims.claim<String>("roles")?.takeIf { it.isNotBlank() }?.split(",") ?: listOf()
                    claims.assert()
                    return@fromCallable SessionItem(
                        an = id,
                        name = claims.audience!!,
                        email = claims.subject!!,
                        roles = roles,
                        ip = ip
                    )
                } catch (_: Exception) {}
            }
            return@fromCallable SessionItem.cast(Account(), ip)
        }

    @Transactional
    override fun updateAuthInfo(sessionItem: SessionItem): Mono<JwtAuthInfoItem> =
        Mono.just(sessionItem)
            .filter { it.isLogin }
            .flatMap {
                accountRepository.findWithRolesByAn(it.an)
                    ?.let { getAuthInfo(GetJwtAuthInfoCommand(sessionItem = SessionItem.cast(it, sessionItem.ip), makeLoginToken = false)) }
                    ?: Mono.error(SecurityException("유효하지 않은 토큰 정보입니다."))
            }

    @Transactional
    override fun getAuthInfo(cmd: GetJwtAuthInfoCommand): Mono<JwtAuthInfoItem> {
        val token = if (cmd.makeLoginToken) { loginTokenRepository.save(LoginToken.create(an = cmd.sessionItem.an)).absoluteToken } else { "" }
        loginFailRepository.deleteByIpAndEmail(cmd.sessionItem.ip, cmd.sessionItem.email)
        loginPassRepository.save(LoginPass.create(an = cmd.sessionItem.an, connType = "login", ip = cmd.sessionItem.ip))
        return Mono.just(JwtAuthInfoItem(toJwt(cmd.sessionItem), token))
    }

    private fun getKey(): JwtKeyItem =
        keyStore.elementAt(1)

    private fun findKey(kid: String): JwtKey? =
        keyStore.find { it.kid == kid }?.key

    private fun toJwt(sessionItem: SessionItem): String = try {
        val keyItem = getKey()
        val claims = JwtClaims.create()
            .id(sessionItem.an.toString())
            .subject(sessionItem.email)
            .audience(sessionItem.name)
            .claim("roles", sessionItem.roles.joinToString(","))
            .expire(OffsetDateTime.now().plusMinutes(180))
        es256.toJwt(keyItem.key, claims, keyItem.kid)
    } catch (e: Exception) {
        throw SecurityException(e.message)
    }
}
