package kr.hhplus.be.server.controller

import kr.hhplus.be.server.InvalidQueueTokenException
import kr.hhplus.be.server.application.QueueService
import kr.hhplus.be.server.application.concert.ConcertService
import kr.hhplus.be.server.controller.model.request.SeatReservationRequest
import kr.hhplus.be.server.controller.model.response.ReservationAvailableDatesResponse
import kr.hhplus.be.server.controller.model.response.ReservationAvailableSeatListResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/reservation")
class ReservationController(
    private val concertService: ConcertService,
    private val queueService: QueueService,
) {

    @GetMapping("/available-dates")
    fun getAvailableDates(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestHeader("X-QUEUE-TOKEN-ID") queueTokenId: String,
        @RequestParam("concert-id") concertId: String,
    ): ResponseEntity<ReservationAvailableDatesResponse> {
        if (queueService.getStatus(queueTokenId).isAllowedToEnter) throw InvalidQueueTokenException()
        return ResponseEntity.ok(ReservationAvailableDatesResponse.from(concertService.getAvailableDates(concertId)))
    }


    @GetMapping("/available-seats")
    fun getAvailableSeats(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestHeader("X-QUEUE-TOKEN-ID") queueTokenId: String,
        @RequestParam("date") date: LocalDate,
    ): ResponseEntity<ReservationAvailableSeatListResponse> {
        return ResponseEntity.ok(ReservationAvailableSeatListResponse.mockResponse)
    }

    @PostMapping("")
    fun reserveSeat(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestHeader("X-QUEUE-TOKEN-ID") queueTokenId: String,
        @RequestBody request: SeatReservationRequest,
    ): ResponseEntity<Void> {
        return ResponseEntity.noContent().build()
    }
}
