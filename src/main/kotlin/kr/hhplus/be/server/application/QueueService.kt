package kr.hhplus.be.server.application

import kr.hhplus.be.server.AlreadyAssignedQueueAccountException
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
}