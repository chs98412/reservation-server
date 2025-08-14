package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.common.exception.AlreadyReservedSeatException
import kr.hhplus.be.server.common.exception.NotFoundConcertException
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.infrastructure.acquireLockOrThrow
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReserveSeatService(
    private val reservationRepository: ReservationRepository,
) : ReserveSeatUseCase {
    @Transactional
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