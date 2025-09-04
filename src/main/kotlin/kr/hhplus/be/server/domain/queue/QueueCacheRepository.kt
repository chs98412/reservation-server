package kr.hhplus.be.server.domain.queue

interface QueueCacheRepository {
    fun addActive(concertId: Long, accountId: String, ttlMinutes: Long)
    fun existsInActive(concertId: Long, accountId: String): Boolean
    fun removeFromActive(concertId: Long, accountId: String)
}
