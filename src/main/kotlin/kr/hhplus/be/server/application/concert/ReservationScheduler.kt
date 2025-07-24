package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.ConcertRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ReservationCleanupScheduler(
    private val concertRepository: ConcertRepository
) {
    @Scheduled(fixedRate = 60_000)
    fun releaseExpiredReservations() {
        concertRepository.findAllByStatus("RESERVED").forEach { it.expireIfNeeded() }
    }
}