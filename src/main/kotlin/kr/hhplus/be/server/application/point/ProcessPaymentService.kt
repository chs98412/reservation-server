package kr.hhplus.be.server.application.point

import kr.hhplus.be.server.common.exception.NotFoundBalanceException
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.point.BalanceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProcessPaymentService(
    private val balanceRepository: BalanceRepository,
    private val reservationRepository: ReservationRepository,
) : ProcessPaymentUseCase {

    @Transactional
    override fun execute(accountId: String) {
        val balance = balanceRepository.findByAccountId(accountId) ?: throw NotFoundBalanceException()
        val reservations = reservationRepository.findAllByAccountIdAndStatus(accountId, "RESERVED")
        val totalPrice = reservations.sumOf { it.price }
        balance.deduct(totalPrice)
        reservations.forEach { it.markAsReserved() }
    }
}