package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.common.exception.NotFoundConcertException
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.ConcertScheduleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GetAvailableDatesService(
    private val concertRepository: ConcertRepository,
    private val concertScheduleRepository: ConcertScheduleRepository,
) : GetAvailableDatesUseCase {
    override fun execute(concertId: Long): ReservationAvailableDatesResponse {
        concertRepository.findByIdOrNull(concertId) ?: throw NotFoundConcertException()
        return concertScheduleRepository.findAllByConcertId(concertId).let { //TODO 매진된 날짜는 반환하지 않아야할것 같음
            ReservationAvailableDatesResponse.from(it)
        }
    }
}