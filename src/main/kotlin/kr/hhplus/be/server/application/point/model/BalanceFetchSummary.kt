package kr.hhplus.be.server.application.point.model

import kr.hhplus.be.server.domain.point.Balance

data class BalanceFetchSummary(
    val point: Long,
) {
    companion object {
        fun from(balance: Balance) = BalanceFetchSummary(
            point = balance.getAmount()
        )
    }
}
