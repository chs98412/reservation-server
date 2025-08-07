package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.Reservation
import java.time.LocalDate

interface GetAvailableSeatsUseCase {
    fun execute(concertId: Long, date: LocalDate): AvailableConcertReservationFetchResponse
}

data class AvailableConcertReservationFetchResponse(
    val availableReservationIdList: List<Long>,
) {
    companion object {
        fun from(reservations: List<Reservation>) = AvailableConcertReservationFetchResponse(
            availableReservationIdList = reservations.map { it.id }
        )
    }
}