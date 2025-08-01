package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.domain.queue.QueueStateRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
class QueueScheduler(
    private val queueStateRepository: QueueStateRepository
) {

    @Scheduled(fixedRate = QueueToken.SCHEDULE_INTERVAL)
    @Transactional
    fun increaseEntranceNumber() {
        queueStateRepository.findAll().forEach {
            it.incrementEntranceNumber(amount = QueueToken.QUEUE_ENTRANCE_LIMIT)
        }
    }
}