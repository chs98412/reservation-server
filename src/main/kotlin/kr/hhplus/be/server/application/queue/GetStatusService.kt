package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.domain.queue.QueueCacheRepository
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
                isAllowedToEnter = true,
            )
        }

        return QueueStatusResponse.from(
            isAllowedToEnter = false,
        )
    }
}
