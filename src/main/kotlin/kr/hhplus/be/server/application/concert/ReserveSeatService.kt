package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.common.exception.AlreadyReservedSeatException
import kr.hhplus.be.server.common.exception.NotFoundConcertException
import kr.hhplus.be.server.domain.concert.ReservationRepository
import org.springframework.stereotype.Service

@Service
class ReserveSeatService(
    private val reservationRepository: ReservationRepository,
) : ReserveSeatUseCase {
    override fun execute(command: SeatReservationCommand) {
        reservationRepository.findByConcertIdAndSeatNo(
            command.concertId,
            command.seatNo
        )
            ?.let {
                if (it.isUnAvailableToReserve()) throw AlreadyReservedSeatException()
                it.reserve(command.accountId)
            } ?: throw NotFoundConcertException()
    }
}