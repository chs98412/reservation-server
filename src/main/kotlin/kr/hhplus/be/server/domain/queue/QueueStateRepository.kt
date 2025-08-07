package kr.hhplus.be.server.domain.queue

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository

@Repository
interface QueueStateRepository : JpaRepository<QueueState, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByConcertId(concertId: Long): QueueState?
}