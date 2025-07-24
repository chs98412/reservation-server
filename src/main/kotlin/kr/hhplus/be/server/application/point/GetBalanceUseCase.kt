package kr.hhplus.be.server.application.point

import kr.hhplus.be.server.domain.point.Balance

interface GetBalanceUseCase {
    fun execute(accountId: String): BalanceFetchResponse
}

data class PaymentRequest(
    val reservationId: Int,
)

data class BalanceFetchResponse(
    val point: Long,
) {
    companion object {
        fun from(balance: Balance) = BalanceFetchResponse(
            point = balance.getAmount()
        )
    }
}