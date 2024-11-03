package anissia.domain.session.command

import anissia.domain.session.model.SessionItem

class GetJwtAuthInfoCommand(
    val sessionItem: SessionItem,
    val makeLoginToken: Boolean
)
