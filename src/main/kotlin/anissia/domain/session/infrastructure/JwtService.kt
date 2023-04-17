package anissia.domain.session.infrastructure

import anissia.domain.session.core.model.JwtKeyItem
import anissia.domain.session.core.ports.outbound.JwtKeyPairRepository
import me.saro.jwt.alg.es.JwtEs256
import me.saro.jwt.core.JwtAlgorithm
import me.saro.jwt.core.JwtKey
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

@Service
class JwtService(
    val algorithm: JwtAlgorithm = JwtEs256(),
    private val jwtKeyPairRepository: JwtKeyPairRepository,
    private val keyStore: Deque<JwtKeyItem> = ConcurrentLinkedDeque(),
) {
    fun updateKeyStore() {
        val initSize = keyStore.size

        val list = jwtKeyPairRepository.findAllByOrderByKidDesc()
            .reversed()
            .map { JwtKeyItem(it.kid.toString(), algorithm.toJwtKey(it.data)) }

        list.forEach {
            if (!keyStore.contains(it)) {
                keyStore.addFirst(it)
            }
        }

        val sumSize = keyStore.size

        keyStore.removeIf { !list.contains(it) }

        log.info("jwt key updated: total(${list.size}) new(${sumSize - initSize}) remove(${keyStore.size - sumSize})")
    }

    fun getKeyItem(): JwtKeyItem =
        keyStore.elementAt(2)

    fun findKey(kid: String): JwtKey? =
        keyStore.find { it.kid == kid }?.key
}
