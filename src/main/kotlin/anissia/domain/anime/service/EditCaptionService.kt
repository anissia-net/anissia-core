package anissia.domain.anime.service

import anissia.domain.anime.AnimeCaption
import anissia.domain.anime.model.EditCaptionCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.session.model.Session
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
            edit(
                episode = cmd.episode,
                updDt = cmd.updLdt.atOffset(ZoneOffset.ofHours(9)),
                website = cmd.website
            )
        })

        return ResultWrapper.of("ok", "자막정보가 반영되었습니다.")
    }
}
