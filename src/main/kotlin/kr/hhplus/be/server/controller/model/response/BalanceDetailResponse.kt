package kr.hhplus.be.server.controller.model.response

import kr.hhplus.be.server.application.point.model.BalanceFetchSummary

data class BalanceDetailResponse(
    val point: Long
) {
    companion object {
        fun from(summary: BalanceFetchSummary) = BalanceDetailResponse(
            point = summary.point,
        )
    }
}
