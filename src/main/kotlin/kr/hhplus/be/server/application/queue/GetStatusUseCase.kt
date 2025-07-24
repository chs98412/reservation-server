package kr.hhplus.be.server.application.queue

interface GetStatusUseCase {
    fun execute(queueTokenId: String): QueueStatusResponse
}

data class QueueStatusResponse(
    val queueNumber: Long,
    val isAllowedToEnter: Boolean,
    val estimateWaitTime: Long,
) {
    companion object {
        fun from(
            queueNumber: Long,
            isAllowedToEnter: Boolean,
            estimateWaitTime: Long
        ) = QueueStatusResponse(
            queueNumber = queueNumber,
            isAllowedToEnter = isAllowedToEnter,
            estimateWaitTime = estimateWaitTime
        )
    }
}