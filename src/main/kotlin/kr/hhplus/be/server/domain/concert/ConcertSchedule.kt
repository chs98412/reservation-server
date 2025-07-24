package kr.hhplus.be.server.domain.concert

import java.time.LocalDate

class ConcertSchedule(
    val id: Long,
    val concertId: Long,
    val date: LocalDate,
    val totalSeats: Int = 50
)