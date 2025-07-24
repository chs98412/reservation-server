package kr.hhplus.be.server.controller.model.response

data class QueueStatusDetailResponse(
    val queueNumber: Long,
    val estimatedWaitSeconds: Long,
    val status: String, //TODO 추후 Enum으로 수정 필요
) {
    companion object {
        val mockResponse = QueueStatusDetailResponse(
            queueNumber = 100,
            estimatedWaitSeconds = 1000,
            status = "WAITING"
        )
    }
}
