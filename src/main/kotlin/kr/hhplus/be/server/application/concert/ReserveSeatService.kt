package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.common.exception.AlreadyReservedSeatException
import kr.hhplus.be.server.common.exception.NotFoundConcertException
import kr.hhplus.be.server.domain.concert.ConcertRepository
import org.springframework.stereotype.Service

@Service
class ReserveSeatService(
    private val concertRepository: ConcertRepository
) : ReserveSeatUseCase {
    override fun execute(command: SeatReservationCommand) {
        concertRepository.findByConcertIdAndScheduleIdAndSeatNo(command.concertId, command.scheduleId, command.seatNo)
            ?.let {
                if (it.isUnAvailableToReserve()) throw AlreadyReservedSeatException()
                it.reserve(command.accountId)
            } ?: throw NotFoundConcertException()
    }
}