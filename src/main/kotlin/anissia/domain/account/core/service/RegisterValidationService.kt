package anissia.domain.account.core.service

import anissia.domain.account.core.Account
import anissia.domain.account.core.AccountRegisterAuth
import anissia.domain.account.core.model.RegisterCommand
import anissia.domain.account.core.model.RegisterValidationCommand
import anissia.domain.account.core.ports.inbound.RegisterValidation
import anissia.domain.account.core.ports.outbound.AccountRegisterAuthRepository
import anissia.domain.account.core.ports.outbound.AccountRepository
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class RegisterValidationService(
    private val accountRegisterAuthRepository: AccountRegisterAuthRepository,
    private val accountRepository: AccountRepository,
): RegisterValidation {

    @Transactional
    override fun handle(cmd: RegisterValidationCommand): ResultWrapper<Unit> {
        cmd.validate()

        val auth: AccountRegisterAuth = accountRegisterAuthRepository.findByNoAndTokenAndExpDtAfterAndUsedDtNull(cmd.tn, cmd.token, OffsetDateTime.now())
            ?: return ResultWrapper.fail("이메일 인증이 만료되었습니다.")

        val registerCommand = As.OBJECT_MAPPER.readValue(auth.data, object: TypeReference<RegisterCommand>() {})

        if (accountRepository.existsByEmail(registerCommand.email)) {
            return ResultWrapper.fail("이미 가입된 계정입니다.")
        }

        if (accountRepository.existsByName(registerCommand.name)) {
            return ResultWrapper.fail("사용중인 닉네임 입니다.")
        }

        accountRegisterAuthRepository.save(auth.apply { usedDt = OffsetDateTime.now() })

        accountRepository.save(
            Account(
                email = registerCommand.email,
                password = registerCommand.password,
                name = registerCommand.name
            )
        )

        return ResultWrapper.ok()
    }

}
