package kr.hhplus.be.server.eventListener.concert

import kr.hhplus.be.server.application.dataPlatform.DataPlatformService
import kr.hhplus.be.server.common.exception.NotFoundReservationException
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.eventListener.concert.model.ReservationEvent
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ConcertEventListener(
    private val reservationRepository: ReservationRepository,
    private val dataPlatformService: DataPlatformService,
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleReserveEvent(event: ReservationEvent) {
        reservationRepository.findByIdOrNull(event.reservationId)?.let {
            dataPlatformService.sendData(it)
        } ?: throw NotFoundReservationException()
    }
}