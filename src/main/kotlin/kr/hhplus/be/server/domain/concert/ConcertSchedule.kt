package kr.hhplus.be.server.domain.concert

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "concert_schedule")
class ConcertSchedule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "concert_id", nullable = false)
    val concertId: Long,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(name = "total_seats", nullable = false)
    val totalSeats: Int = 50,
)