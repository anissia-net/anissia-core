package anissia.infrastructure.service

import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class BCryptService {
    val salt: String = BCrypt.gensalt(10)

    fun encode(password: String): String {
        return BCrypt.hashpw(password, salt)
    }

    fun matches(encrypt: String, text: String): Boolean {
        return BCrypt.checkpw(text, encrypt)
    }
}
