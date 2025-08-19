package kr.hhplus.be.server.application.queue

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.exception.AlreadyAssignedQueueAccountException
import kr.hhplus.be.server.domain.queue.QueueToken
import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service

@Service
class CreateTokenService(
    private val queueTokenSigner: QueueTokenSigner,
    private val redisson: RedissonClient,
) : CreateTokenUseCase {
    @Transactional
    override fun execute(accountId: String, concertId: Long): QueueTokenResponse {
        val waiting: RScoredSortedSet<String> = redisson.getScoredSortedSet("waiting:$concertId")
        val active = redisson.getMapCache<String, String>("active:$concertId")

        if (waiting.contains(accountId) || active.containsKey(accountId)) {
            throw AlreadyAssignedQueueAccountException()
        }

        val queueNumber = System.currentTimeMillis().toDouble()
        waiting.add(queueNumber, accountId)

        val signedToken = QueueToken.create(accountId, queueNumber.toLong()).let(queueTokenSigner::encode)
        return QueueTokenResponse.from(signedToken)
    }
}