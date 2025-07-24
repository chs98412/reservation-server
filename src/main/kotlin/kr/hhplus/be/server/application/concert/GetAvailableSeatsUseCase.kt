package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.Reservation
import java.time.LocalDate

interface GetAvailableSeatsUseCase {
    fun execute(concertId: String, date: LocalDate): AvailableConcertReservationFetchResponse
}

data class AvailableConcertReservationFetchResponse(
    val availableConcertIdList: List<Long>,
) {
    companion object {
        fun from(reservations: List<Reservation>) = AvailableConcertReservationFetchResponse(
            availableConcertIdList = reservations.map { it.id }
        )
    }
}