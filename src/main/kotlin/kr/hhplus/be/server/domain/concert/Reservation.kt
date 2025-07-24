package kr.hhplus.be.server.domain.concert

import java.time.LocalDate
import java.time.LocalDateTime

class Reservation(
    val id: Long,
    val concertId: String,
    val scheduleId: Long,
    val seatNo: Int,
    val date: LocalDate,
    accountId: String? = null,
    status: String = "AVAILABLE",
    val price: Long = 1000, //TODO 등급별로 변동 가능하도록 수정
) {
    var accountId: String? = accountId
        private set
    var status: String = status //TODO enum으로 변경
        private set
    var reservedAt: LocalDateTime? = null
        private set

    fun markAsReserved() {
        status = "RESERVED"
    }

    fun markAsPaid() {
        status = "PAID"
    }

    fun isUnAvailableToReserve(): Boolean {
        return status != "AVAILABLE"
    }

    fun reserve(accountId: String) {
        this.accountId = accountId
        status = "RESERVED"
        reservedAt = LocalDateTime.now()
    }

    fun expireIfNeeded() {
        if (reservedAt != null && reservedAt!!.plusMinutes(EXPIRATION_MINUTES).isBefore(LocalDateTime.now())) {
            status = "AVAILABLE"
            accountId = null
            reservedAt = null
        }
    }

    companion object {
        private const val EXPIRATION_MINUTES = 5L
    }
}