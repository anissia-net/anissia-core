package anissia.domain.anime.service

import anissia.domain.activePanel.model.AddTextActivePanelCommand
import anissia.domain.activePanel.service.ActivePanelService
import anissia.domain.anime.AnimeCaption
import anissia.domain.anime.model.DeleteCaptionCommand
import anissia.domain.anime.model.UpdateAnimeDocumentCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeRepository
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteCaptionService(
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeRepository: AnimeRepository,
    private val updateAnimeDocument: UpdateAnimeDocument,
    private val activePanelService: ActivePanelService
): DeleteCaption {
    @Transactional
    override fun handle(cmd: DeleteCaptionCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateAdmin()

        val animeNo = cmd.animeNo

        return animeCaptionRepository.findByIdOrNull(AnimeCaption.Key(animeNo, session.an))
            ?.run {
                animeCaptionRepository.delete(this)
                animeRepository.updateCaptionCount(animeNo)
                updateAnimeDocument.handle(UpdateAnimeDocumentCommand(animeNo))
                activePanelService.addText(AddTextActivePanelCommand("[${session.name}]님이 [${anime?.subject}] 자막을 종료하였습니다.", true), null)
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("이미 삭제되었습니다.")
    }
}
