package kr.hhplus.be.server.domain.queue

import org.springframework.stereotype.Repository

@Repository
interface QueueRepository {
    fun assignQueueNumber(accountId: String): Long?
    fun incrementEntranceNumber(amount: Long)
    fun getQueueNumber(accountId: String): Long?
    fun getCurrentEntranceNumber(): Long
}