package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.concert.Status
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReservationCleanupScheduler(
    private val reservationRepository: ReservationRepository,
) {
    @Scheduled(fixedRate = 60_000)
    @Transactional
    fun releaseExpiredReservations() {
        reservationRepository.findAllByStatus(Status.RESERVED).forEach { it.expireIfNeeded() }
    }
}