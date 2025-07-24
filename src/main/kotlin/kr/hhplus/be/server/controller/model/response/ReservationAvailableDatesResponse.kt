package kr.hhplus.be.server.controller.model.response

import kr.hhplus.be.server.application.concert.model.ConcertScheduleFetchSummary
import java.time.LocalDate


data class ReservationAvailableDatesResponse(
    val availableDates: List<LocalDate>
) {
    companion object {
        fun from(summary: ConcertScheduleFetchSummary) =
            ReservationAvailableDatesResponse(availableDates = summary.availableDates)

    }
}