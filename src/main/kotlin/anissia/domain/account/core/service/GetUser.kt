package anissia.domain.account.core.service

import anissia.domain.account.core.model.AccountUserItem
import anissia.domain.session.core.model.Session

interface GetUser {
    fun handle(session: Session): AccountUserItem
}
