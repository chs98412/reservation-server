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
    status: String = "AVAILABLE",
    val price: Long = 1000, //TODO 등급별로 변동 가능하도록 수정
    val reservedAt: LocalDateTime = LocalDateTime.now()
) {
    var status: String = "AVAILABLE" //TODO enum으로 변경
        private set

    fun markAsReserved() {
        status = "RESERVED"
    }

    fun markAsPaid() {
        status = "PAID"
    }

}