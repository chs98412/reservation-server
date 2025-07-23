package kr.hhplus.be.server.application.point

import kr.hhplus.be.server.NotFoundBalanceException
import kr.hhplus.be.server.application.point.model.BalanceFetchSummary
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.point.BalanceRepository
import org.springframework.stereotype.Service

@Service
class PayService(
    private val balanceRepository: BalanceRepository,
    private val concertRepository: ConcertRepository,
) {
    fun charge(accountId: String, amount: Long) {
        balanceRepository.findByAccountId(accountId)?.also {
            it.charge(amount)
        } ?: throw NotFoundBalanceException()
    }

    fun getBalance(accountId: String): BalanceFetchSummary {
        return balanceRepository.findByAccountId(accountId)?.let(BalanceFetchSummary::from)
            ?: throw NotFoundBalanceException()
    }

    //TODO 로직 고민 필요, 결제 api를 외부 api 사용한다고 생각하고 트랜잭션 고민해봐야할듯
    fun processPayment(accountId: String) {
        val balance = balanceRepository.findByAccountId(accountId) ?: throw NotFoundBalanceException()
        val reservations = concertRepository.findAllByUserIdAndStatus(accountId, "RESERVED")
        val totalPrice = reservations.sumOf { it.price }
        balance.deduct(totalPrice)
        reservations.forEach { it.markAsReserved() }
    }
}