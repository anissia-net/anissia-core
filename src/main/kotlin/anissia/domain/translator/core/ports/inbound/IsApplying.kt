package anissia.domain.translator.core.ports.inbound

import anissia.domain.session.core.model.Session

interface IsApplying {
    fun handle(session: Session): Boolean
}
