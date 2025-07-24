package kr.hhplus.be.server.controller.model.response

import kr.hhplus.be.server.application.model.QueueStatusSummary

data class QueueStatusDetailResponse(
    val queueNumber: Long,
    val isAllowedToEnter: Boolean,
    val estimateWaitTime: Long,
) {
    companion object {
        fun from(summary: QueueStatusSummary) = QueueStatusDetailResponse(
            queueNumber = summary.queueNumber,
            isAllowedToEnter = summary.isAllowedToEnter,
            estimateWaitTime = summary.estimateWaitTime,
        )
    }
}
