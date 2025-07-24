package kr.hhplus.be.server.controller.model.response

import kr.hhplus.be.server.application.model.QueueTokenSummary

data class QueueTokenCreateResponse(
    val token: String,
) {
    companion object {
        fun from(summary: QueueTokenSummary) = QueueTokenCreateResponse(
            token = summary.token,
        )
    }
}
