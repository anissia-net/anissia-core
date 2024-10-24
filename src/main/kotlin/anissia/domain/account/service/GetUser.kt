package anissia.domain.account.service

import anissia.domain.account.model.AccountUserItem
import anissia.domain.session.model.Session

interface GetUser {
    fun handle(session: Session): AccountUserItem
}
