package kr.hhplus.be.server.domain.queue

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QueueStateRepository : JpaRepository<QueueState, Long> {
    fun findByConcertId(concertId: Long): QueueState?
}