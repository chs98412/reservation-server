package kr.hhplus.be.server.domain.queue

interface QueueCacheRepository {
    fun addWaiting(concertId: Long, accountId: String, score: Double)
    fun getRank(concertId: Long, accountId: String): Int?
    fun existsInWaiting(concertId: Long, accountId: String): Boolean
    fun addActive(concertId: Long, accountId: String, ttlMinutes: Long)
    fun existsInActive(concertId: Long, accountId: String): Boolean
    fun pollFirstWaiting(concertId: Long): String?
    fun removeFromActive(concertId: Long, accountId: String)
}
