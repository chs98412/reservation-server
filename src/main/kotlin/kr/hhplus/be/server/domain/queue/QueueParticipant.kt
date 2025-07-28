package kr.hhplus.be.server.domain.queue

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "queue_participant")
class QueueParticipant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "account_id", unique = true, nullable = false)
    val accountId: String,

    @Column(name = "queue_number", nullable = false)
    val queueNumber: Long,

    @Column(name = "created_at", insertable = false, updatable = false)
    val createdAt: LocalDateTime? = null
)
