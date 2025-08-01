package kr.hhplus.be.server.presentation.model

import kr.hhplus.be.server.application.concert.SeatReservationCommand

data class SeatReservationRequest(
    val concertId: Long,
    val seatNo: Int,
) {
    fun toCommand(accountId: String) = SeatReservationCommand(
        accountId = accountId,
        concertId = concertId,
        seatNo = seatNo,
    )
}