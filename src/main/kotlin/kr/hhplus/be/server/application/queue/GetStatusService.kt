package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.common.exception.AccountNotFoundInQueueException
import kr.hhplus.be.server.common.exception.QueueNotFoundException
import kr.hhplus.be.server.domain.queue.QueueParticipantRepository
import kr.hhplus.be.server.domain.queue.QueueStateRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetStatusService(
    private val queueTokenSigner: QueueTokenSigner,
    private val participantRepository: QueueParticipantRepository,
    private val queueStateRepository: QueueStateRepository
) : GetStatusUseCase {

    @Transactional(readOnly = true)
    override fun execute(queueTokenId: String): QueueStatusResponse {
        val queueToken = queueTokenSigner.decode(queueTokenId)
        val queueNumber = participantRepository.findByAccountId(queueToken.accountId)?.queueNumber
            ?: throw AccountNotFoundInQueueException()
        val currentEntranceNumber =
            queueStateRepository.findByConcertId(queueToken.concertId)?.entranceNumber ?: throw QueueNotFoundException()

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