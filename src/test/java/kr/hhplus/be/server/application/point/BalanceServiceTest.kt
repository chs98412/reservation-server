package kr.hhplus.be.server.application.point

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.NotFoundBalanceException
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.Reservation
import kr.hhplus.be.server.domain.point.Balance
import kr.hhplus.be.server.domain.point.BalanceRepository

class BalanceServiceTest : BehaviorSpec({

    val balanceRepository = mockk<BalanceRepository>()
    val concertRepository = mockk<ConcertRepository>()

    val payService = PayService(balanceRepository, concertRepository)

    Given("존재하지 않는 accountId가 주어졌을 때") {
        val invalidAccountId = "9999"
        val chargeAmount = 10L

        every { balanceRepository.findByAccountId(invalidAccountId) } returns null

        When("충전을 시도하면") {
            Then("NotFoundBalanceException이 발생해야 한다") {
                shouldThrow<NotFoundBalanceException> {
                    payService.charge(invalidAccountId, chargeAmount)
                }
            }
        }
    }


    Given("결제 프로세스에서") {
        val balance = mockk<Balance>(relaxed = true)
        val reservation1 = mockk<Reservation>(relaxed = true)
        val reservation2 = mockk<Reservation>(relaxed = true)

        every { reservation1.price } returns 100
        every { reservation2.price } returns 200

        every { balanceRepository.findByAccountId(any()) } returns balance
        every {
            concertRepository.findAllByUserIdAndStatus(any(), any())
        } returns listOf(reservation1, reservation2)

        When("processPayment를 호출하면") {
            payService.processPayment("accountId")

            Then("잔액 차감과 예약 상태 변경이 호출되어야 한다") {
                verify(exactly = 1) { balance.deduct(300) }
                verify(exactly = 1) { reservation1.markAsReserved() }
                verify(exactly = 1) { reservation2.markAsReserved() }
            }
        }
    }
})