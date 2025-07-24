package kr.hhplus.be.server.controller.model.response

data class BalanceDetailResponse(
    val balance: Long
) {
    companion object {
        val mockResponse = BalanceDetailResponse(
            balance = 20000
        )
    }
}
