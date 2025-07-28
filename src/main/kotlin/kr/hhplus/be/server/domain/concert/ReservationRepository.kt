package kr.hhplus.be.server.domain.concert

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ReservationRepository : JpaRepository<Reservation, Long> {
    fun findAllByConcertIdAndDateAndStatus(concertId: Long, date: LocalDate, status: String): List<Reservation>
    fun findAllByAccountIdAndStatus(accountId: String, status: String): List<Reservation>
    fun findByConcertIdAndScheduleIdAndSeatNo(concertId: Long, scheduleId: Long, seatNo: Int): Reservation?
    fun findAllByStatus(status: String): List<Reservation>
}
