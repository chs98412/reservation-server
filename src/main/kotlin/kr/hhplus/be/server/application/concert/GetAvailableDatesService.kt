package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.common.exception.NotFoundConcertException
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.concert.Status
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GetAvailableDatesService(
    private val concertRepository: ConcertRepository,
    private val reservationRepository: ReservationRepository,
) : GetAvailableDatesUseCase {
    override fun execute(concertId: Long): ReservationAvailableDatesResponse {
        concertRepository.findByIdOrNull(concertId) ?: throw NotFoundConcertException()
        return reservationRepository.findAllByStatus(Status.AVAILABLE).map { it.date }.distinct()
            .let { ReservationAvailableDatesResponse.from(it) }
    }
}