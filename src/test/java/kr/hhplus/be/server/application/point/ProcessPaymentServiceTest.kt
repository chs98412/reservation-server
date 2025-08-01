package kr.hhplus.be.server.application.point

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domain.concert.Reservation
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.point.Balance
import kr.hhplus.be.server.domain.point.BalanceRepository

class ProcessPaymentServiceTest : BehaviorSpec({

    val balanceRepository = mockk<BalanceRepository>()
    val reservationRepository = mockk<ReservationRepository>()

    val processPaymentService = ProcessPaymentService(balanceRepository, reservationRepository)

    Given("결제 프로세스에서") {
        val balance = mockk<Balance>(relaxed = true)
        val reservation1 = mockk<Reservation>(relaxed = true)
        val reservation2 = mockk<Reservation>(relaxed = true)

        every { reservation1.price } returns 100
        every { reservation2.price } returns 200

        every { balanceRepository.findByAccountId(any()) } returns balance
        every {
            reservationRepository.findAllByAccountIdAndStatus(any(), any())
        } returns listOf(reservation1, reservation2)

        When("processPayment를 호출하면") {
            processPaymentService.execute("accountId")

            Then("잔액 차감과 예약 상태 변경이 호출되어야 한다") {
                verify(exactly = 1) { balance.deduct(300) }
                verify(exactly = 1) { reservation1.markAsPaid() }
                verify(exactly = 1) { reservation2.markAsPaid() }
            }
        }
    }
})