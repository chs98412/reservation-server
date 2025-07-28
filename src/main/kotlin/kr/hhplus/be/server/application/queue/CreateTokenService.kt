package kr.hhplus.be.server.application.queue

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.exception.AlreadyAssignedQueueAccountException
import kr.hhplus.be.server.common.exception.QueueNotFoundException
import kr.hhplus.be.server.domain.queue.QueueParticipant
import kr.hhplus.be.server.domain.queue.QueueParticipantRepository
import kr.hhplus.be.server.domain.queue.QueueStateRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import org.springframework.stereotype.Service

@Service
class CreateTokenService(
    private val queueTokenSigner: QueueTokenSigner,
    private val participantRepository: QueueParticipantRepository,
    private val queueStateRepository: QueueStateRepository
) : CreateTokenUseCase {
    @Transactional
    override fun execute(accountId: String, concertId: Long): QueueTokenResponse {

        if (participantRepository.existByAccountId(accountId)) {
            throw AlreadyAssignedQueueAccountException()
        }

        val queueNumber =
            queueStateRepository.findByConcertId(concertId)
                ?.apply { increaseTotalParticipantCount() }?.totalParticipantCount
                ?: throw QueueNotFoundException()

        participantRepository.save(QueueParticipant(accountId = accountId, queueNumber = queueNumber))
        val signedToken = QueueToken.create(accountId, queueNumber).let(queueTokenSigner::encode)

        return QueueTokenResponse.from(signedToken = signedToken)
    }
}