package anissia.dto.request

import me.saro.kit.Texts
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator


internal class AccountRecoverPasswordRequestTest {

    private var validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    @DisplayName("@Valid - 계정복구(암호찾기) 암호입력")
    fun valid() {

        val tn = (Math.random() * Long.MAX_VALUE).toLong().toString()
        val token = Texts.createRandomString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray(), 128, 512)
        var password = Texts.createRandomString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-=_+ ".toCharArray(), 8, 128)

        val sut = AccountRecoverPasswordRequest("$tn-$token", password)

        val result = validator.validate(sut)

        assert(result.isEmpty()) { "$sut\n\n$result" }
    }
}