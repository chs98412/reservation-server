package kr.hhplus.be.server.application.queue

interface CreateTokenUseCase {
    fun execute(accountId: String): QueueTokenResponse
}

data class QueueTokenResponse(
    val token: String,
) {
    companion object {
        fun from(signedToken: String) = QueueTokenResponse(
            token = signedToken
        )
    }
}