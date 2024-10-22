package anissia.domain.session.service

import anissia.domain.account.repository.AccountRepository
import anissia.domain.session.model.GetLoginInfoItemCommand
import anissia.domain.session.model.LoginInfoItem
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.stereotype.Service

@Service
class UpdateJwtService(
    private val accountRepository: AccountRepository,
    private val getLoginInfoItem: GetLoginInfoItem
): UpdateJwt {
    override fun handle(session: Session): ResultWrapper<LoginInfoItem> {
        if (session.isLogin) {
            accountRepository.findWithRolesByAn(session.an)
                ?.run { getLoginInfoItem.handle(GetLoginInfoItemCommand(session = Session.cast(this, session.ip), makeLoginToken = false)) }
                ?.run { return@handle this }
        }

        return ResultWrapper.fail("유효하지 않은 토큰 정보입니다.", null)
    }
}
