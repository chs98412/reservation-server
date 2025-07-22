package kr.hhplus.be.server.domain.concert

import java.time.LocalDate
import java.time.LocalDateTime

class Reservation(
    val id: Long,
    val concertId: String,
    val scheduleId: Long,
    val seatNumber: Int,
    val date: LocalDate,
    val userId: String? = null,
    val status: String = "AVAILABLE", //TODO enum으로 변경
    val reservedAt: LocalDateTime = LocalDateTime.now()
)