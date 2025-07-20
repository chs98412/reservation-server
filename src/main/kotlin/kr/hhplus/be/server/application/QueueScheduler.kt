package kr.hhplus.be.server.application

import kr.hhplus.be.server.domain.queue.QueueRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class QueueScheduler(
    private val queueRepository: QueueRepository,
) {

    @Scheduled(fixedRate = 10000)
    fun increaseEntranceNumber() {
        queueRepository.incrementEntranceNumber(amount = 30)
    }
}