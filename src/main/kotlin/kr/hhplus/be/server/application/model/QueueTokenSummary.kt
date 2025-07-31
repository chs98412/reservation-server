package kr.hhplus.be.server.application.model

data class QueueTokenSummary(
    val token: String,
) {
    companion object {
        fun from(signedToken: String) = QueueTokenSummary(
            token = signedToken
        )
    }
}
