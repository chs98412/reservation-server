package kr.hhplus.be.server.infrastructure

import kr.hhplus.be.server.domain.concert.ConcertRankCacheRepository
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Repository

@Repository
class ConcertRankCacheRepositoryImpl(
    private val redisson: RedissonClient
) : ConcertRankCacheRepository {
    private val key = "top-sell"

    override fun getTop(limit: Int): List<Long> {
        val z = redisson.getScoredSortedSet<String>(key)
        return z.entryRangeReversed(0, limit - 1)
            .map { it.value.toLong() }
    }

    override fun clear() {
        redisson.getScoredSortedSet<String>(key).clear()
    }

    override fun add(concertId: Long, score: Double) {
        redisson.getScoredSortedSet<String>(key).add(score, concertId.toString())
    }
}
