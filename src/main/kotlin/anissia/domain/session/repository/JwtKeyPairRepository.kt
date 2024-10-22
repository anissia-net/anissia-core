package anissia.domain.session.repository

import anissia.domain.session.JwtKeyPair
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

interface JwtKeyPairRepository: JpaRepository<JwtKeyPair, Long> {
    fun findAllByOrderByKidDesc(page: Pageable = PageRequest.of(0, 24)): List<JwtKeyPair>

    @Transactional
    @Modifying
    @Query("DELETE FROM JwtKeyPair WHERE kid < :kid")
    fun deleteAllByKidBefore(kid: Long = OffsetDateTime.now().minusHours(3).toInstant().toEpochMilli())
}
