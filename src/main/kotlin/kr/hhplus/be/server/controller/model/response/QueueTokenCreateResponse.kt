package kr.hhplus.be.server.controller.model.response

import java.time.LocalDateTime

data class QueueTokenCreateResponse(
    val tokenId: String,
    val queueNumber: Long,
    val estimatedWaitSeconds: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        val mockResponse = QueueTokenCreateResponse(
            tokenId = "bb7de087-2e5d-4b6c-b7c4-bb3b97360d24",
            queueNumber = 100,
            estimatedWaitSeconds = 1000,
            createdAt = LocalDateTime.now()
        )
    }
}
