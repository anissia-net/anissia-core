package anissia.domain.session.core.service

import anissia.domain.account.core.repository.AccountRepository
import anissia.domain.session.core.model.GetLoginInfoItemCommand
import anissia.domain.session.core.model.LoginInfoItem
import anissia.domain.session.core.model.Session
import anissia.domain.session.core.ports.inbound.GetLoginInfoItem
import anissia.domain.session.core.ports.inbound.UpdateJwt
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
