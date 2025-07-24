package kr.hhplus.be.server.controller.model.response

import kr.hhplus.be.server.application.concert.model.AvailableConcertReservationFetchSummary

data class ReservationAvailableSeatListResponse(
    val availableConcertIdList: List<Long>
) {
    companion object {
        fun from(summary: AvailableConcertReservationFetchSummary) = ReservationAvailableSeatListResponse(
            availableConcertIdList = summary.availableConcertIdList
        )
    }
}