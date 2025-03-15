package anissia.domain.session.service

import anissia.domain.session.model.SessionItem
import anissia.domain.store.Store
import anissia.domain.store.repository.StoreRepository
import me.saro.jwt.Jwt
import me.saro.jwt.JwtNode
import me.saro.jwt.store.JwtKeyStoreProvider
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.OffsetDateTime

@Service
class JwtServiceImpl(
    private val storeRepository: StoreRepository,
): JwtService {
    private val store: JwtKeyStoreProvider = Jwt
        .createKeyStoreProvider()
        .algorithm(Jwt.ES256)
        .keyExpireTime(Duration.ofMinutes(180))
        .build(storeRepository.findByIdOrNull("jwt.key")?.data ?: "[]")

    override fun issue() {
        store.issue()
        val storeData = storeRepository.findByIdOrNull("jwt.key")
            ?: Store("jwt.key", "", "[]")
        storeRepository.save(storeData.apply { data = store.exports() })
    }

    override fun parseJwt(jwt: String): JwtNode = store.parseJwt(jwt)

    override fun createJwt(sessionItem: SessionItem): String = try {
        store.createJwt()
            .id(sessionItem.an.toString())
            .subject(sessionItem.email)
            .audience(sessionItem.name)
            .claim("roles", sessionItem.roles.joinToString(","))
            .expire(OffsetDateTime.now().plusMinutes(180))
            .build()
    } catch (e: Exception) {
        throw SecurityException(e.message)
    }
}
