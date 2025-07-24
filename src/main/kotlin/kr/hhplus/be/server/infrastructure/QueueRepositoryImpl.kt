package kr.hhplus.be.server.infrastructure

import kr.hhplus.be.server.domain.queue.QueueRepository

class QueueRepositoryImpl : QueueRepository {
    private val participantSet = mutableSetOf<String>()
    private val queuePositionMap = mutableMapOf<String, Long>()
    private var counter = 0L

    override fun assignQueueNumber(accountId: String): Long? {
        if (participantSet.contains(accountId)) {
            return null
        }

        counter += 1
        participantSet.add(accountId)
        queuePositionMap[accountId] = counter
        return counter
    }
}