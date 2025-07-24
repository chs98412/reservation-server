package kr.hhplus.be.server.domain.point

class Balance(
    val accountId: Long,
) {
    private var amount: Long = 0

    fun charge(amount: Long) {
        this.amount += amount
    }

    fun getAmount(): Long = amount
}