package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.ConcertSchedule
import java.time.LocalDate

interface GetAvailableDatesUseCase {
    fun execute(concertId: Long): ReservationAvailableDatesResponse
}

data class ReservationAvailableDatesResponse(
    val availableDates: List<LocalDate>
) {
    companion object {
        fun from(concertSchedule: List<ConcertSchedule>) =
            ReservationAvailableDatesResponse(availableDates = concertSchedule.map { it.date })
    }
}