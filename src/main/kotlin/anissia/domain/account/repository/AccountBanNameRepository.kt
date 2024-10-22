package anissia.domain.account.repository

import anissia.domain.account.AccountBanName
import org.springframework.data.jpa.repository.JpaRepository

interface AccountBanNameRepository : JpaRepository<AccountBanName, String> { //, QuerydslPredicateExecutor<AccountBanName>
}
