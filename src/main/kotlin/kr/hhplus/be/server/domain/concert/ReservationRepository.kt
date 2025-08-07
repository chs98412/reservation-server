package kr.hhplus.be.server.domain.concert

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ReservationRepository : JpaRepository<Reservation, Long> {
    fun findAllByConcertIdAndDateAndStatus(concertId: Long, date: LocalDate, status: Status): List<Reservation>
    fun findAllByAccountIdAndStatus(accountId: String, status: Status): List<Reservation>
    fun findByConcertIdAndSeatNo(concertId: Long, seatNo: Int): Reservation?
    fun findAllByStatus(status: Status): List<Reservation>
}
