package kr.hhplus.be.server.domain.concert


import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "concert")
class Concert(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    val endDate: LocalDate,
)