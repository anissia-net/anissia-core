package anissia.domain.session.model

class GetLoginInfoItemCommand(
    val session: Session,
    val makeLoginToken: Boolean
)
