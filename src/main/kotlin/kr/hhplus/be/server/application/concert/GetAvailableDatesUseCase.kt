package kr.hhplus.be.server.application.concert

import java.time.LocalDate

interface GetAvailableDatesUseCase {
    fun execute(concertId: Long): ReservationAvailableDatesResponse
}

data class ReservationAvailableDatesResponse(
    val availableDates: List<LocalDate>
) {
    companion object {
        fun from(concertSchedules: List<LocalDate>) =
            ReservationAvailableDatesResponse(availableDates = concertSchedules)
    }
}