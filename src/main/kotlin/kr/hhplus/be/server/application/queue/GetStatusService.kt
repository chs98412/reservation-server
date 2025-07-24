package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.common.exception.QueueNotFoundException
import kr.hhplus.be.server.domain.queue.QueueRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import org.springframework.stereotype.Service

@Service
class GetStatusService(
    private val queueRepository: QueueRepository,
    private val queueTokenSigner: QueueTokenSigner,
) : GetStatusUseCase {

    override fun execute(queueTokenId: String): QueueStatusResponse {
        val queueToken = queueTokenSigner.decode(queueTokenId)
        val queueNumber = queueRepository.getQueueNumber(queueToken.accountId)
            ?: throw QueueNotFoundException()
        val currentEntranceNumber = queueRepository.getCurrentEntranceNumber()

        return QueueStatusResponse.from(
            queueNumber = queueNumber,
            isAllowedToEnter = canEnter(queueNumber, currentEntranceNumber, QueueToken.EXPIRE_THRESHOLD),
            estimateWaitTime = estimateWaitTime(
                queueNumber,
                currentEntranceNumber,
                QueueToken.EXPIRE_THRESHOLD,
                QueueToken.SCHEDULE_INTERVAL
            )
        )
    }

    private fun canEnter(queueNumber: Long, entranceNumber: Long, expiredThreshold: Long): Boolean {
        if (queueNumber > entranceNumber) return false
        if (queueNumber < entranceNumber - expiredThreshold) return false
        return true
    }

    private fun estimateWaitTime(
        queueNumber: Long,
        entranceNumber: Long,
        batchSize: Long,
        interval: Long
    ): Long {
        val remaining = (queueNumber - entranceNumber).coerceAtLeast(0)
        val batchCount = (remaining + batchSize - 1) / batchSize
        return batchCount * interval
    }
}