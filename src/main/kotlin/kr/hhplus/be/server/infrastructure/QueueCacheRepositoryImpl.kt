package kr.hhplus.be.server.infrastructure

import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class QueueCacheRepositoryImpl(
    private val redisson: RedissonClient
) : QueueCacheRepository {
    override fun addActive(concertId: Long, accountId: String, ttlMinutes: Long) {
        val key = "active:$concertId:$accountId"
        val bucket = redisson.getBucket<String>(key)
        bucket.set("ACTIVE", Duration.ofMinutes(ttlMinutes))
    }

    override fun existsInActive(concertId: Long, accountId: String): Boolean {
        val key = "active:$concertId:$accountId"
        return redisson.getBucket<String>(key).isExists
    }

    override fun removeFromActive(concertId: Long, accountId: String) {
        val key = "active:$concertId:$accountId"
        redisson.getBucket<String>(key).delete()
    }
}
