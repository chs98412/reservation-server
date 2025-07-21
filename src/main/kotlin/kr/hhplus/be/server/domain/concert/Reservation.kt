package kr.hhplus.be.server.domain.concert

import java.time.LocalDateTime

class Reservation(
    val id: Long,
    val scheduleId: Long,
    val seatNumber: Int,
    val userId: String,
    val reservedAt: LocalDateTime = LocalDateTime.now()
)