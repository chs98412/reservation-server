package kr.hhplus.be.server.application.point

import kr.hhplus.be.server.common.exception.NotFoundBalanceException
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.point.BalanceRepository
import org.springframework.stereotype.Service

@Service
class ProcessPaymentService(
    private val balanceRepository: BalanceRepository,
    private val concertRepository: ConcertRepository,
) : ProcessPaymentUseCase {

    //TODO 로직 고민 필요, 결제 api를 외부 api 사용한다고 생각하고 트랜잭션 고민해봐야할듯
    override fun execute(accountId: String) {
        val balance = balanceRepository.findByAccountId(accountId) ?: throw NotFoundBalanceException()
        val reservations = concertRepository.findAllByUserIdAndStatus(accountId, "RESERVED")
        val totalPrice = reservations.sumOf { it.price }
        balance.deduct(totalPrice)
        reservations.forEach { it.markAsReserved() }
    }
}