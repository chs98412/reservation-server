package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.common.exception.AccountNotFoundInQueueException
import kr.hhplus.be.server.domain.queue.QueueToken
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service

@Service
class GetStatusService(
    private val queueTokenSigner: QueueTokenSigner,
    private val redisson: RedissonClient,
) : GetStatusUseCase {

    override fun execute(queueTokenId: String): QueueStatusResponse {
        val token = queueTokenSigner.decode(queueTokenId)
        val concertId = token.concertId

        if (isActiveUser(concertId, token.accountId)) {
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

    private fun isActiveUser(concertId: Long, accountId: String): Boolean {
        val active = redisson.getMapCache<String, String>("active:$concertId")
        return active.containsKey(accountId)
    }

    private fun getQueueNumber(concertId: Long, accountId: String): Long {
        val waiting = redisson.getScoredSortedSet<String>("waiting:$concertId")
        val rank = waiting.rank(accountId) ?: throw AccountNotFoundInQueueException()
        return rank + 1L
    }

    private fun calculateEstimateWaitTime(queueNumber: Long): Long {
        val batchCount = (queueNumber / QueueToken.QUEUE_ENTRANCE_LIMIT) + 1
        return batchCount * QueueToken.SCHEDULE_INTERVAL
    }
}