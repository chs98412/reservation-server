package kr.hhplus.be.server.controller.model.response

import java.time.LocalDate


data class ReservationAvailableDatesResponse(
    val availableDates: List<LocalDate>
) {
    companion object {
        val mockResponse = ReservationAvailableDatesResponse(
            availableDates = listOf(
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 2),
                LocalDate.of(2025, 7, 3)
            )
        )
    }
}