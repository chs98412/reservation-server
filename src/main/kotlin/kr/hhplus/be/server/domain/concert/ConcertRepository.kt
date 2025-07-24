package kr.hhplus.be.server.domain.concert

import org.springframework.stereotype.Repository

@Repository
interface ConcertRepository {
    fun findByConcertId(concertId: String): Concert?
    fun findAllByConcertId(concertId: String): List<ConcertSchedule>
}