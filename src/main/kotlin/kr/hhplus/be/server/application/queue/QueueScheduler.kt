package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import kr.hhplus.be.server.domain.queue.QueueToken.Companion.EXPIRE_THRESHOLD
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class QueueScheduler(
    private val concertRepository: ConcertRepository,
    private val queueCacheRepository: QueueCacheRepository,
) {
    @Scheduled(fixedRate = QueueToken.SCHEDULE_INTERVAL)
    fun increaseEntranceNumber() {
        concertRepository.findAll().forEach { concert ->
            repeat(QueueToken.QUEUE_ENTRANCE_LIMIT.toInt()) {
                val user = queueCacheRepository.pollFirstWaiting(concert.id) ?: return@repeat
                queueCacheRepository.addActive(concert.id, user, EXPIRE_THRESHOLD)
            }
        }
    }
}