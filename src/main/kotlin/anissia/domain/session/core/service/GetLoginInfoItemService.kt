package anissia.domain.session.core.service

import anissia.domain.session.core.LoginPass
import anissia.domain.session.core.LoginToken
import anissia.domain.session.core.model.GetLoginInfoItemCommand
import anissia.domain.session.core.model.LoginInfoItem
import anissia.domain.session.core.model.Session
import anissia.domain.session.core.ports.inbound.GetLoginInfoItem
import anissia.domain.session.core.ports.outbound.LoginFailRepository
import anissia.domain.session.core.ports.outbound.LoginPassRepository
import anissia.domain.session.core.ports.outbound.LoginTokenRepository
import anissia.domain.session.infrastructure.JwtService
import anissia.shared.ResultWrapper
import me.saro.jwt.core.JwtClaims
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class GetLoginInfoItemService(
    private val loginTokenRepository: LoginTokenRepository,
    private val loginPassRepository: LoginPassRepository,
    private val loginFailRepository: LoginFailRepository,
    private val jwtService: JwtService
): GetLoginInfoItem {

    @Transactional
    override fun handle(cmd: GetLoginInfoItemCommand): ResultWrapper<LoginInfoItem> {
        val session = cmd.session

        val token = cmd.takeIf { it.makeLoginToken }
            ?.let { LoginToken.create(an = session.an).also { loginTokenRepository.save(it) } }
            ?.absoluteToken
            ?:""

        val jwt = toJwt(session)

        // clean up and return
        loginFailRepository.deleteByIpAndEmail(session.ip, session.email)
        loginPassRepository.save(LoginPass.create(an = session.an, connType = "login", ip = session.ip))

        return ResultWrapper.ok(LoginInfoItem(jwt, token))
    }

    fun toJwt(session: Session): String = try {
        val keyItem = jwtService.getKeyItem()
        val claims = JwtClaims.create()
            .id(session.an.toString())
            .subject(session.email)
            .audience(session.name)
            .claim("roles", session.roles.joinToString(","))
            .expire(OffsetDateTime.now().plusMinutes(180))
        jwtService.algorithm.toJwt(keyItem.key, claims, keyItem.kid)
    } catch (e: Exception) {
        throw SecurityException(e.message)
    }
}
