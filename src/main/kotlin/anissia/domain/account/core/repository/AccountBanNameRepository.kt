package anissia.domain.account.core.repository

import anissia.domain.account.core.AccountBanName
import org.springframework.data.jpa.repository.JpaRepository

interface AccountBanNameRepository : JpaRepository<AccountBanName, String> { //, QuerydslPredicateExecutor<AccountBanName>
}
