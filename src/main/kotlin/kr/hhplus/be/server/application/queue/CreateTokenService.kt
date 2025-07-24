package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.common.exception.AlreadyAssignedQueueAccountException
import kr.hhplus.be.server.domain.queue.QueueRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import org.springframework.stereotype.Service

@Service
class CreateTokenService(
    private val queueRepository: QueueRepository,
    private val queueTokenSigner: QueueTokenSigner,
) : CreateTokenUseCase {
    override fun execute(accountId: String): QueueTokenResponse {

        //TODO 유저 검증 로직
        val queueNumber =
            queueRepository.assignQueueNumber(accountId) ?: throw AlreadyAssignedQueueAccountException()

        val signedToken = QueueToken.create(accountId, queueNumber).let(queueTokenSigner::encode)

        return QueueTokenResponse.from(signedToken = signedToken)
    }
}