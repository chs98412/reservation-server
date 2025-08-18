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


    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    var genre: Genre = Genre.PERFORMANCE,

    @Column(name = "views", nullable = false)
    val views: Long = 0,

    @Column(name = "likes", nullable = false)
    val likes: Long = 0,
)

enum class Genre(val koName: String) {
    PERFORMANCE("공연"),
    MUSICAL("뮤지컬"),
    EXHIBITION("전시"),
    CLASSICAL("클래식"),
    CHILDREN("아동"),
    PLAY("연극");
}