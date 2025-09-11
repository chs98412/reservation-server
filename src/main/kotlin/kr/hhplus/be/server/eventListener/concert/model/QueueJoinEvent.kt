package kr.hhplus.be.server.eventListener.concert.model

data class QueueJoinEvent(
    val concertId: Long,
    val accountId: String,
)