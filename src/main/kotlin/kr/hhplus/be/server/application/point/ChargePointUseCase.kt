package kr.hhplus.be.server.application.point

interface ChargePointUseCase {
    fun execute(accountId: String, amount: Long)
}

data class BalanceChargeRequest(
    val amount: Long
)