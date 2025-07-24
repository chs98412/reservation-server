package kr.hhplus.be.server.domain.concert

import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ConcertRepository {
    fun findByConcertId(concertId: String): Concert?
    fun findAllByConcertId(concertId: String): List<ConcertSchedule>
    fun findAllByConcertIdAndDateAndStatus(concertId: String, date: LocalDate, status: String): List<Reservation>
    fun findAllByUserIdAndStatus(userId: String, status: String): List<Reservation>
}