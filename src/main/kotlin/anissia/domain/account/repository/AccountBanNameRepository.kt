package anissia.domain.account.repository

import anissia.domain.account.AccountBanName
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface AccountBanNameRepository : ReactiveCrudRepository<AccountBanName, String>
