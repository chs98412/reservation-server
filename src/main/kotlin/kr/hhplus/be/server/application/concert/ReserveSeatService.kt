package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.application.kafka.KafkaEventPublisher
import kr.hhplus.be.server.common.exception.AlreadyReservedSeatException
import kr.hhplus.be.server.common.exception.NotFoundConcertException
import kr.hhplus.be.server.domain.KafkaTopic
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.eventListener.concert.model.ReservationEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReserveSeatService(
    private val reservationRepository: ReservationRepository,
    private val kafkaEventPublisher: KafkaEventPublisher,
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
                kafkaEventPublisher.publish(
                    KafkaTopic.RESERVE_COMPLETE,
                    ReservationEvent(reservationId = it.id, accountId = command.accountId)
                )
            } ?: throw NotFoundConcertException()
    }
}