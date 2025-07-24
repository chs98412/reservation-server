package kr.hhplus.be.server.infrastructure

import kr.hhplus.be.server.domain.point.Balance
import kr.hhplus.be.server.domain.point.BalanceRepository
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class BalanceRepositoryImpl : BalanceRepository {

    private val storage = ConcurrentHashMap<String, Balance>()

    override fun findByAccountId(accountId: String): Balance? {
        return storage[accountId]
    }
}