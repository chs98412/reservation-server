package kr.hhplus.be.server.domain.queue

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "queue_state")
class QueueState(
    @Id
    val id: Long = 0,

    val concertId: Long = 1,

    @Column(name = "total_participant_count", nullable = false)
    var totalParticipantCount: Long = 0,

    @Column(name = "entrance_number", nullable = false)
    var entranceNumber: Long = 0
) {
    fun increaseTotalParticipantCount() {
        totalParticipantCount += 1
    }

    fun incrementEntranceNumber(amount: Long) {

    }
}