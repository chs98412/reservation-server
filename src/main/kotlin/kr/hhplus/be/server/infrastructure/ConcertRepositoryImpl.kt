package kr.hhplus.be.server.infrastructure

import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.ConcertSchedule
import kr.hhplus.be.server.domain.concert.Reservation
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

@Component
class ConcertRepositoryImpl : ConcertRepository {
    private val concertStorage = ConcurrentHashMap<String, Concert>()
    private val scheduleStorage = ConcurrentHashMap<String, MutableList<ConcertSchedule>>()
    private val reservationStorage = mutableListOf<Reservation>()

    override fun findByConcertId(concertId: String): Concert? {
        return concertStorage[concertId]
    }

    override fun findAllByConcertId(concertId: String): List<ConcertSchedule> {
        return scheduleStorage[concertId]?.toList() ?: emptyList()
    }

    override fun findAllByConcertIdAndDateAndStatus(
        concertId: String,
        date: LocalDate,
        status: String
    ): List<Reservation> {
        return reservationStorage.filter {
            it.concertId == concertId && it.date == date && it.status == status
        }
    }

}