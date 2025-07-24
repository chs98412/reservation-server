package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.NotFoundConcertException
import kr.hhplus.be.server.application.concert.model.AvailableConcertReservationFetchSummary
import kr.hhplus.be.server.application.concert.model.ConcertScheduleFetchSummary
import kr.hhplus.be.server.domain.concert.ConcertRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ConcertService(
    private val concertRepository: ConcertRepository
) {
    fun getAvailableDates(concertId: String): ConcertScheduleFetchSummary {
        concertRepository.findByConcertId(concertId) ?: throw NotFoundConcertException()
        return concertRepository.findAllByConcertId(concertId).let { //TODO 매진된 날짜는 반환하지 않아야할것 같음
            ConcertScheduleFetchSummary.from(it)
        }
    }

    fun getAvailableSeats(concertId: String, date: LocalDate): AvailableConcertReservationFetchSummary {
        concertRepository.findByConcertId(concertId) ?: throw NotFoundConcertException()
        return concertRepository.findAllByConcertIdAndDateAndStatus(concertId, date, "AVAILABLE").let {
            AvailableConcertReservationFetchSummary.from(it)
        }
    }
}