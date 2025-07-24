package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.common.exception.AlreadyAssignedQueueAccountException
import kr.hhplus.be.server.common.exception.QueueNotFoundException
import kr.hhplus.be.server.application.model.QueueStatusSummary
import kr.hhplus.be.server.application.model.QueueTokenSummary
import kr.hhplus.be.server.domain.queue.QueueRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import org.springframework.stereotype.Service

@Service
class QueueService(
    private val queueRepository: QueueRepository,
    private val queueTokenSigner: QueueTokenSigner,
) {
    fun createToken(accountId: String): QueueTokenSummary {

        //TODO 유저 검증 로직

        val queueNumber =
            queueRepository.assignQueueNumber(accountId) ?: throw AlreadyAssignedQueueAccountException()

        val signedToken = QueueToken.create(accountId, queueNumber).let(queueTokenSigner::encode)

        return QueueTokenSummary.from(signedToken = signedToken)
    }

    fun getStatus(queueTokenId: String): QueueStatusSummary {
        val queueToken = queueTokenSigner.decode(queueTokenId)
        val queueNumber = queueRepository.getQueueNumber(queueToken.accountId)
            ?: throw QueueNotFoundException()
        val currentEntranceNumber = queueRepository.getCurrentEntranceNumber()

        return QueueStatusSummary.from(
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