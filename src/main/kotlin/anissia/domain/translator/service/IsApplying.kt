package anissia.domain.translator.service

import anissia.domain.session.model.SessionItem

interface IsApplying {
    fun handle(sessionItem: SessionItem): Boolean
}
