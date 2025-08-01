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

    @Column(name = "seat_no", nullable = false)
    val seatNo: Int,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(name = "account_id")
    var accountId: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: Status = Status.AVAILABLE,

    @Column(nullable = false)
    val price: Long = 1000,

    @Column(name = "reserved_at")
    var reservedAt: LocalDateTime? = null,
) {
    fun markAsPaid() {
        status = Status.PAID
    }

    fun isUnAvailableToReserve(): Boolean {
        return status != Status.AVAILABLE
    }

    fun reserve(accountId: String) {
        this.accountId = accountId
        this.status = Status.RESERVED
        this.reservedAt = LocalDateTime.now()
    }

    fun expireIfNeeded() {
        if (reservedAt != null && reservedAt!!.plusMinutes(EXPIRATION_MINUTES).isBefore(LocalDateTime.now())) {
            status = Status.AVAILABLE
            accountId = null
            reservedAt = null
        }
    }

    companion object {
        private const val EXPIRATION_MINUTES = 5L
    }
}

enum class Status {
    AVAILABLE,
    RESERVED,
    PAID
}