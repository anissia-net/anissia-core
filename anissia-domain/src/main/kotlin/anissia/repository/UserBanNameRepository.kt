package anissia.repository

import anissia.domain.UserBanName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface UserBanNameRepository : JpaRepository<UserBanName, String>, QuerydslPredicateExecutor<UserBanName>