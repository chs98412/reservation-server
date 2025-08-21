package kr.hhplus.be.server.domain.queue

import org.redisson.api.RMapCache
import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RedissonQueueRepository(
    private val redisson: RedissonClient
) {
    fun addToWaitingQueue(concertId: Long, accountId: String) {
        val zset: RScoredSortedSet<String> = redisson.getScoredSortedSet("waiting:$concertId")
        zset.add(System.currentTimeMillis().toDouble(), accountId)
    }

    fun popFromWaitingQueue(concertId: Long, count: Int): List<String> {
        val zset: RScoredSortedSet<String> = redisson.getScoredSortedSet("waiting:$concertId")
        val items = mutableListOf<String>()
        repeat(count) {
            val first = zset.pollFirst()
            if (first != null) items.add(first)
        }
        return items
    }

    fun addToActiveQueue(concertId: Long, accountId: String, ttlSeconds: Long) {
        val map: RMapCache<String, String> = redisson.getMapCache("active:$concertId")
        map.put(accountId, "ACTIVE", ttlSeconds, TimeUnit.SECONDS)
    }

    fun isActive(concertId: Long, accountId: String): Boolean {
        val map: RMapCache<String, String> = redisson.getMapCache("active:$concertId")
        return map.containsKey(accountId)
    }

    fun getWaitingRank(concertId: Long, accountId: String): Int? {
        val zset: RScoredSortedSet<String> = redisson.getScoredSortedSet("waiting:$concertId")
        return zset.rank(accountId)
    }
}
