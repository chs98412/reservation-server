package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.concert.Status
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GetAvailableSeatsService(
    private val concertRepository: ConcertRepository,
    private val reservationRepository: ReservationRepository,
) : GetAvailableSeatsUseCase {
    override fun execute(concertId: Long, date: LocalDate): AvailableConcertReservationFetchResponse {
//        concertRepository.findByIdOrNull(concertId) ?: throw NotFoundConcertException()
        return reservationRepository.findAllByConcertIdAndDateAndStatus(concertId, date, Status.AVAILABLE).let {
            AvailableConcertReservationFetchResponse.from(it)
        }
    }
}