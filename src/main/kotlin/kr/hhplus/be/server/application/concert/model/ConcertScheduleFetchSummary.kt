package kr.hhplus.be.server.application.concert.model

import kr.hhplus.be.server.domain.concert.ConcertSchedule
import java.time.LocalDate

data class ConcertScheduleFetchSummary(
    val availableDates: List<LocalDate>
) {
    companion object {
        fun from(concertSchedule: List<ConcertSchedule>) =
            ConcertScheduleFetchSummary(availableDates = concertSchedule.map { it.date })
    }
}
