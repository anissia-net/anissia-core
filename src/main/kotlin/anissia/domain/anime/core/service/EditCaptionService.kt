package anissia.domain.anime.core.service

import anissia.domain.anime.core.AnimeCaption
import anissia.domain.anime.core.model.EditCaptionCommand
import anissia.domain.anime.core.ports.inbound.EditCaption
import anissia.domain.anime.core.ports.outbound.AnimeCaptionRepository
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset

@Service
class EditCaptionService(
    private val animeCaptionRepository: AnimeCaptionRepository
): EditCaption {
    @Transactional
    override fun handle(cmd: EditCaptionCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateAdmin()

        val animeNo = cmd.animeNo

        val caption = animeCaptionRepository.findByIdOrNull(AnimeCaption.Key(animeNo, session.an))
            ?: return ResultWrapper.fail("존재하지 않는 자막입니다.")

        animeCaptionRepository.save(caption.apply {
            this.episode = cmd.episode
            this.updDt = cmd.updLdt.atOffset(ZoneOffset.ofHours(9))
            this.website = cmd.website
        })

        return ResultWrapper.of("ok", "자막정보가 반영되었습니다.")
    }
}
