package anissia.domain.session.service

import anissia.domain.account.repository.AccountRepository
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
import anissia.shared.ResultWrapper
import me.saro.jwt.alg.es.JwtEs256
import me.saro.jwt.core.Jwt
import me.saro.jwt.core.JwtClaims
import me.saro.jwt.core.JwtKey
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

@Service
class JwtServiceImpl(
    private val jwtKeyPairRepository: JwtKeyPairRepository,
    private val keyStore: Deque<JwtKeyItem> = ConcurrentLinkedDeque(),
    private val accountRepository: AccountRepository,
    private val loginTokenRepository: LoginTokenRepository,
    private val loginPassRepository: LoginPassRepository,
    private val loginFailRepository: LoginFailRepository,
): JwtService {
    private val es256: JwtEs256 = Jwt.es256()

    override fun renewKeyStore() {
        val list = jwtKeyPairRepository.findAllByOrderByKidDesc()
            .reversed()
            .map { JwtKeyItem(it.kid.toString(), es256.toJwtKeyByStringify(it.data)) }

        list.forEach {
            if (!keyStore.contains(it)) {
                keyStore.addFirst(it)
            }
        }
        keyStore.removeIf { !list.contains(it) }
    }

    override fun getKeyItem(): JwtKeyItem =
        keyStore.elementAt(1)

    override fun getKey(kid: String): JwtKey? =
        keyStore.find { it.kid == kid }?.key

    @Transactional
    override fun updateAuthInfo(sessionItem: SessionItem): ResultWrapper<JwtAuthInfoItem> {
        if (sessionItem.isLogin) {
            accountRepository.findWithRolesByAn(sessionItem.an)
                ?.run { getAuthInfo(GetJwtAuthInfoCommand(sessionItem = SessionItem.cast(this, sessionItem.ip), makeLoginToken = false)) }
                ?.run { return@updateAuthInfo this }
        }

        return ResultWrapper.fail("유효하지 않은 토큰 정보입니다.", null)
    }

    override fun alg(): JwtEs256 = es256

    @Transactional
    override fun getAuthInfo(cmd: GetJwtAuthInfoCommand): ResultWrapper<JwtAuthInfoItem> {
        val session = cmd.sessionItem

        val token = cmd.takeIf { it.makeLoginToken }
            ?.let { LoginToken.create(an = session.an).also { loginTokenRepository.save(it) } }
            ?.absoluteToken
            ?:""

        val jwt = toJwt(session)

        // clean up and return
        loginFailRepository.deleteByIpAndEmail(session.ip, session.email)
        loginPassRepository.save(LoginPass.create(an = session.an, connType = "login", ip = session.ip))

        return ResultWrapper.ok(JwtAuthInfoItem(jwt, token))
    }

    fun toJwt(sessionItem: SessionItem): String = try {
        val keyItem = getKeyItem()
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
