package kr.hhplus.be.server.application.concert

interface ReserveSeatUseCase {
    fun execute(command: SeatReservationCommand)
}

data class SeatReservationCommand(
    val accountId: String,
    val concertId: String,
    val scheduleId: Long,
    val seatNo: Int,
)