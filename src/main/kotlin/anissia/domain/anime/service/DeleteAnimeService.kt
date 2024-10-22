package anissia.domain.anime.service

import anissia.domain.activePanel.model.NewActivePanelTextCommand
import anissia.domain.activePanel.service.NewActivePanelText
import anissia.domain.agenda.Agenda
import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.anime.model.AnimeItem
import anissia.domain.anime.model.DeleteAnimeCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeRepository
import anissia.domain.session.model.Session
import anissia.domain.translator.service.GetPassedDate
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class DeleteAnimeService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val updateAnimeDocument: UpdateAnimeDocument,
    private val newActivePanelText: NewActivePanelText,
    private val agendaRepository: AgendaRepository,
    private val getPassedDate: GetPassedDate,
): DeleteAnime {

    @Transactional
    override fun handle(cmd: DeleteAnimeCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateAdmin()
        getPassedDate.handle(session.an)
            ?.takeIf { it.isBefore(OffsetDateTime.now().minusDays(90)) }
            ?: return ResultWrapper.fail("애니메이션 삭제는 권한 취득일로부터 90일 후에 가능합니다.")

        val animeNo = cmd.animeNo
        val agenda = Agenda(code = "ANIME-DEL", status = "wait", an = session.an)

        val anime = animeRepository.findWithCaptionsByAnimeNo(animeNo)
            ?.also { agenda.data1 = As.toJsonString(AnimeItem(it, true)) }
            ?: return ResultWrapper.fail("존재하지 않는 애니메이션입니다.")

        newActivePanelText.handle(NewActivePanelTextCommand("[${session.name}]님이 애니메이션 [${anime.subject}]을(를) 삭제하였습니다."), null)

        animeCaptionRepository.deleteByAnimeNo(animeNo)
        animeRepository.delete(anime)
        updateAnimeDocument.handle(anime)
        agendaRepository.save(agenda)

        return ResultWrapper.ok()
    }
}
