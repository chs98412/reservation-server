package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.common.exception.AccountNotFoundInQueueException
import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import org.springframework.stereotype.Service

@Service
class GetStatusService(
    private val queueTokenSigner: QueueTokenSigner,
    private val queueCacheRepository: QueueCacheRepository,
) : GetStatusUseCase {

    override fun execute(queueTokenId: String): QueueStatusResponse {
        val token = queueTokenSigner.decode(queueTokenId)
        val concertId = token.concertId

        if (queueCacheRepository.existsInActive(concertId, token.accountId)) {
            return QueueStatusResponse.from(
                queueNumber = token.queueNumber,
                isAllowedToEnter = true,
                estimateWaitTime = 0
            )
        }

        val queueNumber = getQueueNumber(concertId, token.accountId)
        val estimateWaitTime = calculateEstimateWaitTime(queueNumber)

        return QueueStatusResponse.from(
            queueNumber = queueNumber,
            isAllowedToEnter = false,
            estimateWaitTime = estimateWaitTime
        )
    }

    private fun getQueueNumber(concertId: Long, accountId: String): Long {
        val rank = queueCacheRepository.getRank(concertId, accountId)
            ?: throw AccountNotFoundInQueueException()
        return rank + 1L
    }

    private fun calculateEstimateWaitTime(queueNumber: Long): Long {
        val batchCount = (queueNumber / QueueToken.QUEUE_ENTRANCE_LIMIT) + 1
        return batchCount * QueueToken.SCHEDULE_INTERVAL
    }
}
