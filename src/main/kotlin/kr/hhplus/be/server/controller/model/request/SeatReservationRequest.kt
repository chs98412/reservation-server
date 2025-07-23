package kr.hhplus.be.server.controller.model.request

import kr.hhplus.be.server.application.concert.model.SeatReservationCommand

data class SeatReservationRequest(
    val concertId: String,
    val scheduleId: Long,
    val seatNo: Int,
) {
    fun toCommand(accountId: String) = SeatReservationCommand(
        accountId = accountId,
        concertId = concertId,
        scheduleId = scheduleId,
        seatNo = seatNo,
    )
}