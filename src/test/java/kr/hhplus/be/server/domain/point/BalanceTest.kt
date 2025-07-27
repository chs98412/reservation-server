package kr.hhplus.be.server.domain.point

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.longs.shouldBeExactly
import kr.hhplus.be.server.common.exception.InsufficientBalanceException

class BalanceTest : BehaviorSpec({

    Given("초기 금액이 0인 Balance가 있을 때") {
        val balance = Balance(balanceId = 1L, accountId = "accountId", amount = 0L)

        When("100원을 충전하면") {
            balance.charge(100)

            Then("잔액은 100원이 된다") {
                balance.amount shouldBeExactly 100L
            }
        }

        When("추가로 50원을 충전하면") {
            balance.charge(50)

            Then("잔액은 150원이 된다") {
                balance.amount shouldBeExactly 150L
            }
        }
    }
    Given("잔액이 부족한 Balance가 주어졌을 때") {
        val balance = Balance(balanceId = 1L, accountId = "accountId", amount = 1L)

        When("100원을 차감하면") {
            Then("InsufficientBalanceException이 발생해야 한다") {
                shouldThrow<InsufficientBalanceException> {
                    balance.deduct(100)
                }
            }
        }
    }
})