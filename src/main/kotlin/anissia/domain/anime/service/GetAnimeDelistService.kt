package anissia.domain.anime.service

import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.anime.model.AnimeItem
import anissia.domain.session.model.Session
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
