package kr.hhplus.be.server.domain.point

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.longs.shouldBeExactly

class BalanceTest : BehaviorSpec({

    Given("초기 금액이 0인 Balance가 있을 때") {
        val balance = Balance(accountId = 1L)

        When("100원을 충전하면") {
            balance.charge(100)

            Then("잔액은 100원이 된다") {
                balance.getAmount() shouldBeExactly 100L
            }
        }

        When("추가로 50원을 충전하면") {
            balance.charge(50)

            Then("잔액은 150원이 된다") {
                balance.getAmount() shouldBeExactly 150L
            }
        }
    }
})