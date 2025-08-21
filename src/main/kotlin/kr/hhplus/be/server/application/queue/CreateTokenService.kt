package kr.hhplus.be.server.application.queue

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.exception.AlreadyAssignedQueueAccountException
import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import org.springframework.stereotype.Service

@Service
class CreateTokenService(
    private val queueTokenSigner: QueueTokenSigner,
    private val queueCacheRepository: QueueCacheRepository,
) : CreateTokenUseCase {
    @Transactional
    override fun execute(accountId: String, concertId: Long): QueueTokenResponse {
        if (queueCacheRepository.existsInWaiting(concertId, accountId) ||
            queueCacheRepository.existsInActive(concertId, accountId)
        ) {
            throw AlreadyAssignedQueueAccountException()
        }

        val queueNumber = System.currentTimeMillis().toDouble()
        queueCacheRepository.addWaiting(concertId, accountId, queueNumber)

        val signedToken = QueueToken.create(accountId, queueNumber.toLong())
            .let(queueTokenSigner::encode)
        return QueueTokenResponse.from(signedToken)
    }
}
