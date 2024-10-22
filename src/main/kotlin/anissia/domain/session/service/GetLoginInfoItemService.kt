package anissia.domain.session.service

import anissia.domain.session.LoginPass
import anissia.domain.session.LoginToken
import anissia.domain.session.model.GetLoginInfoItemCommand
import anissia.domain.session.model.LoginInfoItem
import anissia.domain.session.model.Session
import anissia.domain.session.repository.LoginFailRepository
import anissia.domain.session.repository.LoginPassRepository
import anissia.domain.session.repository.LoginTokenRepository
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
        jwtService.es256.toJwt(keyItem.key, claims, keyItem.kid)
    } catch (e: Exception) {
        throw SecurityException(e.message)
    }
}
