package kr.hhplus.be.server.domain.point

import kr.hhplus.be.server.InsufficientBalanceException

class Balance(
    val accountId: Long,
) {
    private var amount: Long = 0

    fun charge(amount: Long) {
        this.amount += amount
    }

    fun deduct(price: Long) {
        if (amount < price) throw InsufficientBalanceException()
        amount -= price
    }

    fun getAmount(): Long = amount
}