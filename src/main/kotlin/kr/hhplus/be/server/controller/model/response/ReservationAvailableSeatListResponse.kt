package kr.hhplus.be.server.controller.model.response

data class ReservationAvailableSeatListResponse(
    val availableSeats: List<Int>
) {
    companion object {
        val mockResponse = ReservationAvailableSeatListResponse(
            availableSeats = listOf(1, 2, 3)
        )
    }
}