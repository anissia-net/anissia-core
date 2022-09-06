package anissia.services

import anissia.configruration.logger
import anissia.dto.AccountUserDto
import anissia.dto.ResultStatus
import anissia.dto.request.AccountUpdateNameRequest
import anissia.dto.request.AccountUpdatePasswordRequest
import anissia.rdb.entity.Agenda
import anissia.rdb.repository.AccountBanNameRepository
import anissia.rdb.repository.AccountRepository
import anissia.rdb.repository.AgendaRepository
import anissia.rdb.repository.AnimeCaptionRepository
import org.slf4j.Logger
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 *
 */
@Service
class AccountService (
    private val sessionService: SessionService,
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
    private val agendaRepository: AgendaRepository,
    private val translatorService: TranslatorService,
    private val animeService: AnimeService,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val activePanelService: ActivePanelService,
    private val accountBanNameRepository: AccountBanNameRepository
) {
    private val log: Logger = logger<AccountService>()
    private val an get() = sessionService.session!!.an
    private val codeUpdateName = "AC-UPD-NAME"

    fun getUser() = AccountUserDto.cast(accountRepository.findWithRolesByAn(an)!!)

    fun updateUserPassword(accountUpdatePasswordRequest: AccountUpdatePasswordRequest): ResultStatus {
        val account = getUserTakeIfPassword(accountUpdatePasswordRequest.oldPassword)
            ?: return ResultStatus("FAIL", "기존 암호가 일치하지 않습니다.")

        account.password = passwordEncoder.encode(accountUpdatePasswordRequest.newPassword)
        accountRepository.save(account)
        return ResultStatus("OK")
    }

    @Transactional
    fun updateUserName(accountUpdateNameRequest: AccountUpdateNameRequest): ResultStatus {
        val account = getUserTakeIfPassword(accountUpdateNameRequest.password)
            ?: return ResultStatus("FAIL", "기존 암호가 일치하지 않습니다.")

        val oldName = account.name
        val newName = accountUpdateNameRequest.name

        if (translatorService.existDoingApply()) {
            return ResultStatus("FAIL", "자막제작자 신청중에는 이름을 바꿀 수 없습니다.")
        }

        if (agendaRepository.existsByCodeAndStatusAndAnAndUpdDtAfter(codeUpdateName, "DONE", an, LocalDateTime.now().minusDays(1))) {
            return ResultStatus("FAIL", "이름은 하루에 한번만 바꿀 수 있습니다.")
        }

        if (accountRepository.existsByName(newName) || accountBanNameRepository.existsById(newName)) {
            return ResultStatus("FAIL", "사용중이거나 사용할 수 없는 이름입니다.")
        }

        agendaRepository.saveAndFlush(Agenda(
            code = codeUpdateName,
            status = "DONE",
            an = an,
            data1 = "DONE",
            data2 = oldName,
            data3 = newName,
        ))

        accountRepository.saveAndFlush(account.apply { name = newName })

        // 운영진
        if (account.roles.isNotEmpty()) {
            animeCaptionRepository.findAllByAn(an).forEach {
                animeService.updateDocument(it.anime?.animeNo?:0)
            }
            activePanelService.saveText("운영진 [$oldName]님의 닉네임이 [$newName]님으로 변경되었습니다.", true)
        }

        return ResultStatus("OK")
    }

    private fun getUserTakeIfPassword(password: String) =
        accountRepository.findByIdOrNull(an)
            ?.takeIf { passwordEncoder.matches(password, it.password) }
}
