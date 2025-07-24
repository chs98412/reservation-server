package kr.hhplus.be.server.application.concert.model

data class SeatReservationCommand(
    val accountId: String,
    val concertId: String,
    val scheduleId: Long,
    val seatNo: Int,
)
