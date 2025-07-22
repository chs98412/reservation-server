package kr.hhplus.be.server.application

import kr.hhplus.be.server.domain.queue.QueueRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class QueueScheduler(
    private val queueRepository: QueueRepository,
) {

    @Scheduled(fixedRate = QueueToken.SCHEDULE_INTERVAL)
    fun increaseEntranceNumber() {
        queueRepository.incrementEntranceNumber(amount = QueueToken.QUEUE_ENTRANCE_LIMIT)
    }
}