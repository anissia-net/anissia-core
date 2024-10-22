package anissia.domain.translator.service

import anissia.domain.session.model.Session

interface IsApplying {
    fun handle(session: Session): Boolean
}
