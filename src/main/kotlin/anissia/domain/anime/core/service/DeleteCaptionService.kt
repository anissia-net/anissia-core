package anissia.domain.anime.core.service

import anissia.domain.activePanel.core.model.NewActivePanelTextCommand
import anissia.domain.activePanel.core.ports.inbound.NewActivePanelText
import anissia.domain.anime.core.AnimeCaption
import anissia.domain.anime.core.model.DeleteCaptionCommand
import anissia.domain.anime.core.model.UpdateAnimeDocumentCommand
import anissia.domain.anime.core.ports.inbound.DeleteCaption
import anissia.domain.anime.core.ports.inbound.UpdateAnimeDocument
import anissia.domain.anime.core.ports.outbound.AnimeCaptionRepository
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteCaptionService(
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeRepository: AnimeRepository,
    private val updateAnimeDocument: UpdateAnimeDocument,
    private val newActivePanelText: NewActivePanelText
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
                newActivePanelText.handle(NewActivePanelTextCommand("[${session.name}]님이 [${anime?.subject}] 자막을 종료하였습니다.", true), null)
                ResultWrapper.ok()
            }
            ?: ResultWrapper.fail("이미 삭제되었습니다.")
    }
}
