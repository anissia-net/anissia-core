package anissia.domain.session.core.model

class GetLoginInfoItemCommand(
    val session: Session,
    val makeLoginToken: Boolean
)
