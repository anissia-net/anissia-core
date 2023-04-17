package anissia.domain.anime.core.service

import anissia.domain.agenda.core.ports.outbound.AgendaRepository
import anissia.domain.anime.core.model.AnimeItem
import anissia.domain.anime.core.ports.inbound.GetAnimeDelist
import anissia.domain.session.core.model.Session
import anissia.infrastructure.common.As
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class GetAnimeDelistService(
    private val agendaRepository: AgendaRepository
): GetAnimeDelist {
    override fun handle(session: Session): Page<AnimeItem> {
        session.validateAdmin()

        return agendaRepository.findAllByCodeAndStatusOrderByAgendaNoDesc("ANIME-DEL", "wait")
            .map { As.OBJECT_MAPPER.readValue(it.data1!!, object: TypeReference<AnimeItem>(){}).apply { this.agendaNo = it.agendaNo } }
    }
}
