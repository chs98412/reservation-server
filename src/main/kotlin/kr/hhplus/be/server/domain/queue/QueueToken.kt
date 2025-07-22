package kr.hhplus.be.server.domain.queue

import java.time.LocalDateTime

class QueueToken(
    val accountId: String,
    val queueNumber: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun create(accountId: String, queueNumber: Long) = QueueToken(
            accountId = accountId,
            queueNumber = queueNumber,
            createdAt = LocalDateTime.now(),
        )

        const val QUEUE_ENTRANCE_LIMIT = 30L
    }
}