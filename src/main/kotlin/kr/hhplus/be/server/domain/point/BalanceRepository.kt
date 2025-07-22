package kr.hhplus.be.server.domain.point

import org.springframework.stereotype.Repository

@Repository
interface BalanceRepository {
    fun findByAccountId(accountId: String): Balance?
}