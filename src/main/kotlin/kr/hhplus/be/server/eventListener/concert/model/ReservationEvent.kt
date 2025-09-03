package kr.hhplus.be.server.eventListener.concert.model

data class ReservationEvent(
    val accountId: String,
    val reservationId: Long,
)