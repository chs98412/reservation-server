package kr.hhplus.be.server.presentation

import kr.hhplus.be.server.application.concert.*
import kr.hhplus.be.server.application.queue.GetStatusUseCase
import kr.hhplus.be.server.common.exception.InvalidQueueTokenException
import kr.hhplus.be.server.domain.concert.Genre
import kr.hhplus.be.server.infrastructure.acquireLockOrThrow
import kr.hhplus.be.server.presentation.model.SeatReservationRequest
import org.redisson.api.RedissonClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/reservation")
class ConcertController(
    private val getStatusUseCase: GetStatusUseCase,
    private val getAvailableDatesUseCase: GetAvailableDatesUseCase,
    private val getAvailableSeatsUseCase: GetAvailableSeatsUseCase,
    private val reserveSeatUseCase: ReserveSeatUseCase,
    private val getRankTopConcertsService: GetRankTopConcertsService,
    private val redisson: RedissonClient,
) {

    @GetMapping("/available-dates")
    fun getAvailableDates(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestHeader("X-QUEUE-TOKEN-ID") queueTokenId: String,
        @RequestParam("concert-id") concertId: Long,
    ): ResponseEntity<ReservationAvailableDatesResponse> {
        if (!getStatusUseCase.execute(queueTokenId).isAllowedToEnter) throw InvalidQueueTokenException()
        return ResponseEntity.ok(getAvailableDatesUseCase.execute(concertId))
    }


    @GetMapping("/available-seats")
    fun getAvailableSeats(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestHeader("X-QUEUE-TOKEN-ID") queueTokenId: String,
        @RequestParam("concert-id") concertId: Long,
        @RequestParam("date") date: LocalDate,
    ): ResponseEntity<AvailableConcertReservationFetchResponse> {
        if (!getStatusUseCase.execute(queueTokenId).isAllowedToEnter) throw InvalidQueueTokenException()

        return ResponseEntity.ok(
            getAvailableSeatsUseCase.execute(
                concertId,
                date
            )
        )
    }

    @PostMapping("")
    fun reserveSeat(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestHeader("X-QUEUE-TOKEN-ID") queueTokenId: String,
        @RequestBody request: SeatReservationRequest,
    ): ResponseEntity<Void> {
        if (!getStatusUseCase.execute(queueTokenId).isAllowedToEnter) throw InvalidQueueTokenException()
        redisson.acquireLockOrThrow(key = "reserve:${request.concertId}:${request.seatNo}") {
            reserveSeatUseCase.execute(request.toCommand(accountId))
        }
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/top-ranked/{genre}")
    fun getTop30ByGenre(@PathVariable genre: Genre): ResponseEntity<List<TopRankedConcertResponse>> {
        return ResponseEntity.ok().body(getRankTopConcertsService.execute(genre))
    }
}
