package anissia.domain.session.core

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import me.saro.jwt.core.JwtKey

@Entity
@Table
class JwtKeyPair (
    @Id
    var kid: Long = 0,
    @Column(length = 1024, nullable = true)
    var data: String
) {
    companion object {
        fun create(kid: Long, jwtKey: JwtKey): JwtKeyPair = JwtKeyPair(kid, jwtKey.stringify)
    }
}
