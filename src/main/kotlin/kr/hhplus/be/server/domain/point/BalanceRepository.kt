package kr.hhplus.be.server.domain.point

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BalanceRepository : JpaRepository<Balance, Long> {
    fun findByAccountId(accountId: String): Balance?
}