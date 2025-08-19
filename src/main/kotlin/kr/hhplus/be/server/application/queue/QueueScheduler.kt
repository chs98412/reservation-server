package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import org.redisson.api.RMapCache
import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit


@Component
class QueueScheduler(
    private val redisson: RedissonClient,
    private val concertRepository: ConcertRepository,
) {

    @Scheduled(fixedRate = QueueToken.SCHEDULE_INTERVAL)
    fun increaseEntranceNumber() {
        concertRepository.findAll().forEach { concert ->

            val waiting: RScoredSortedSet<String> = redisson.getScoredSortedSet("waiting:${concert.id}")
            val active: RMapCache<String, String> = redisson.getMapCache("active:${concert.id}")

            repeat(QueueToken.QUEUE_ENTRANCE_LIMIT.toInt()) {
                val user = waiting.pollFirst() ?: return@repeat
                active.put(user, "ACTIVE", 5L, TimeUnit.MINUTES)
            }
        }

    }
}