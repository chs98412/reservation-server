package kr.hhplus.be.server.application.model

data class QueueStatusSummary(
    val queueNumber: Long,
    val isAllowedToEnter: Boolean,
    val estimateWaitTime: Long,
) {
    companion object {
        fun from(
            queueNumber: Long,
            isAllowedToEnter: Boolean,
            estimateWaitTime: Long
        ) = QueueStatusSummary(
            queueNumber = queueNumber,
            isAllowedToEnter = isAllowedToEnter,
            estimateWaitTime = estimateWaitTime
        )
    }
}
