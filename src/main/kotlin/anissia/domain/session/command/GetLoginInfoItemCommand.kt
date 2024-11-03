package anissia.domain.session.command

import anissia.domain.session.model.Session

class GetLoginInfoItemCommand(
    val session: Session,
    val makeLoginToken: Boolean
)
