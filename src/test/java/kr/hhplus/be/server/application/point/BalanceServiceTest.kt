package kr.hhplus.be.server.application.point

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.NotFoundBalanceException
import kr.hhplus.be.server.domain.point.BalanceRepository

class BalanceServiceTest : BehaviorSpec({

    val balanceRepository = mockk<BalanceRepository>()
    val balanceService = BalanceService(balanceRepository)

    Given("존재하지 않는 accountId가 주어졌을 때") {
        val invalidAccountId = "9999"
        val chargeAmount = 10L

        every { balanceRepository.findByAccountId(invalidAccountId) } returns null

        When("충전을 시도하면") {
            Then("NotFoundBalanceException이 발생해야 한다") {
                shouldThrow<NotFoundBalanceException> {
                    balanceService.charge(invalidAccountId, chargeAmount)
                }
            }
        }
    }
})