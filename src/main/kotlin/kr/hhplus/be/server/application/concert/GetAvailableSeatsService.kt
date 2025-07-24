package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.common.exception.NotFoundConcertException
import kr.hhplus.be.server.domain.concert.ConcertRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GetAvailableSeatsService(
    private val concertRepository: ConcertRepository
) : GetAvailableSeatsUseCase {
    override fun execute(concertId: String, date: LocalDate): AvailableConcertReservationFetchResponse {
        concertRepository.findByConcertId(concertId) ?: throw NotFoundConcertException()
        return concertRepository.findAllByConcertIdAndDateAndStatus(concertId, date, "AVAILABLE").let {
            AvailableConcertReservationFetchResponse.from(it)
        }
    }
}