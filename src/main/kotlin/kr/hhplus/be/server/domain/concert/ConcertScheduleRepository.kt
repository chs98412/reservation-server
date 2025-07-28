package kr.hhplus.be.server.domain.concert

import org.springframework.data.jpa.repository.JpaRepository

interface ConcertScheduleRepository : JpaRepository<ConcertSchedule, Long> {
    fun findAllByConcertId(concertId: Long): List<ConcertSchedule>
}