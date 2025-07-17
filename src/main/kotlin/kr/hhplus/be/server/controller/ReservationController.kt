package kr.hhplus.be.server.controller

import kr.hhplus.be.server.controller.model.response.ReservationAvailableDatesResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reservation")
class ReservationController {

    @GetMapping("/available-dates")
    fun getAvailableDates(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestHeader("X-QUEUE-TOKEN-ID") queueTokenId: String,
        @RequestParam("concert-id") concertId: String,
    ): ResponseEntity<ReservationAvailableDatesResponse> {
        return ResponseEntity.ok(ReservationAvailableDatesResponse.mockResponse)
    }
}
