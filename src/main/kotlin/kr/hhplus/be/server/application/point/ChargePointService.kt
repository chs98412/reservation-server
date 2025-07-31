package kr.hhplus.be.server.application.point

import kr.hhplus.be.server.common.exception.NotFoundBalanceException
import kr.hhplus.be.server.domain.point.BalanceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChargePointService(
    private val balanceRepository: BalanceRepository,
) : ChargePointUseCase {
    @Transactional
    override fun execute(accountId: String, amount: Long) {
        balanceRepository.findByAccountId(accountId)?.also {
            it.charge(amount)
        } ?: throw NotFoundBalanceException()
    }
}