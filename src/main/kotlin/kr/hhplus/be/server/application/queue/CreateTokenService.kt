package kr.hhplus.be.server.application.queue

import jakarta.transaction.Transactional
import kr.hhplus.be.server.application.kafka.KafkaEventPublisher
import kr.hhplus.be.server.common.exception.AlreadyAssignedQueueAccountException
import kr.hhplus.be.server.domain.KafkaTopic
import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import kr.hhplus.be.server.eventListener.concert.model.QueueJoinEvent
import org.springframework.stereotype.Service

@Service
class CreateTokenService(
    private val queueTokenSigner: QueueTokenSigner,
    private val queueCacheRepository: QueueCacheRepository,
    private val kafkaEventPublisher: KafkaEventPublisher,
) : CreateTokenUseCase {
    @Transactional
    override fun execute(accountId: String, concertId: Long): QueueTokenResponse {
        if (queueCacheRepository.existsInActive(concertId, accountId)) {
            throw AlreadyAssignedQueueAccountException()
        }

        kafkaEventPublisher.publish(
            KafkaTopic.QUEUE_JOIN,
            concertId.toString(),
            QueueJoinEvent(concertId = concertId, accountId = accountId)
        )
        val queueNumber = System.currentTimeMillis().toDouble()

        val signedToken = QueueToken.create(accountId, queueNumber.toLong())
            .let(queueTokenSigner::encode)
        return QueueTokenResponse.from(signedToken)
    }
}
