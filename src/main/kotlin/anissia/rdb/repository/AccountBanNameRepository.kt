package anissia.rdb.repository

import anissia.rdb.entity.AccountBanName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface AccountBanNameRepository : JpaRepository<AccountBanName, String>, QuerydslPredicateExecutor<AccountBanName>