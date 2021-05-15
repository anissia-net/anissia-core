package anissia.rdb.repository

import anissia.rdb.entity.ActivePanel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface ActivePanelRepository : JpaRepository<ActivePanel, Long>, QuerydslPredicateExecutor<ActivePanel> {

    fun findAllByOrderByApNoDesc(pageable: Pageable): Page<ActivePanel>
}
