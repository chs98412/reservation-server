package kr.hhplus.be.server.application.queue

interface GetStatusUseCase {
    fun execute(queueTokenId: String): QueueStatusResponse
}

data class QueueStatusResponse(
    val isAllowedToEnter: Boolean,
) {
    companion object {
        fun from(
            isAllowedToEnter: Boolean,
        ) = QueueStatusResponse(
            isAllowedToEnter = isAllowedToEnter,
        )
    }
}