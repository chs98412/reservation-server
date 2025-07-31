package kr.hhplus.be.server.application.concert.model

import kr.hhplus.be.server.domain.concert.Reservation

data class AvailableConcertReservationFetchSummary(
    val availableConcertIdList: List<Long>,
) {
    companion object {
        fun from(reservations: List<Reservation>) = AvailableConcertReservationFetchSummary(
            availableConcertIdList = reservations.map { it.id }
        )
    }
}
