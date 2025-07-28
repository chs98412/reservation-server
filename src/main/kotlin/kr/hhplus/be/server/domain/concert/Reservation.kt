package kr.hhplus.be.server.domain.concert

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "reservation")
class Reservation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "concert_id", nullable = false)
    val concertId: Long,

    @Column(name = "schedule_id", nullable = false)
    val scheduleId: Long,

    @Column(name = "seat_no", nullable = false)
    val seatNo: Int,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(name = "account_id")
    var accountId: String? = null,

    @Column(nullable = false)
    var status: String = "AVAILABLE",

    @Column(nullable = false)
    val price: Long = 1000,

    @Column(name = "reserved_at")
    var reservedAt: LocalDateTime? = null,
) {
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
        this.status = "RESERVED"
        this.reservedAt = LocalDateTime.now()
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
