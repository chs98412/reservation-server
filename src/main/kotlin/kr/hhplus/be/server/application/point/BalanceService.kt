package kr.hhplus.be.server.application.point

import kr.hhplus.be.server.NotFoundBalanceException
import kr.hhplus.be.server.domain.point.BalanceRepository
import org.springframework.stereotype.Service

@Service
class BalanceService(
    private val balanceRepository: BalanceRepository
) {
    fun charge(accountId: String, amount: Long) {
        balanceRepository.findByAccountId(accountId)?.also {
            it.charge(amount)
        } ?: throw NotFoundBalanceException()
    }
}