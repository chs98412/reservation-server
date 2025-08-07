package kr.hhplus.be.server.domain.queue

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QueueParticipantRepository : JpaRepository<QueueParticipant, Long> {
    fun existsByAccountId(accountId: String): Boolean
    fun findByAccountId(accountId: String): QueueParticipant?
}