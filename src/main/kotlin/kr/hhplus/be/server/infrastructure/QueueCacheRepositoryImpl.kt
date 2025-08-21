package kr.hhplus.be.server.infrastructure

import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class QueueCacheRepositoryImpl(
    private val redisson: RedissonClient
) : QueueCacheRepository {
    override fun addWaiting(concertId: Long, accountId: String, score: Double) {
        redisson.getScoredSortedSet<String>("waiting:$concertId").add(score, accountId)
    }

    override fun getRank(concertId: Long, accountId: String): Int? {
        return redisson.getScoredSortedSet<String>("waiting:$concertId").rank(accountId)?.plus(1)
    }

    override fun existsInWaiting(concertId: Long, accountId: String): Boolean {
        return redisson.getScoredSortedSet<String>("waiting:$concertId").contains(accountId)
    }

    override fun addActive(concertId: Long, accountId: String, ttlMinutes: Long) {
        val key = "active:$concertId:$accountId"
        val bucket = redisson.getBucket<String>(key)
        bucket.set("ACTIVE", Duration.ofMinutes(ttlMinutes))
    }

    override fun existsInActive(concertId: Long, accountId: String): Boolean {
        val key = "active:$concertId:$accountId"
        return redisson.getBucket<String>(key).isExists
    }

    override fun pollFirstWaiting(concertId: Long): String? {
        val waiting = redisson.getScoredSortedSet<String>("waiting:$concertId")
        return waiting.pollFirst()
    }
}
