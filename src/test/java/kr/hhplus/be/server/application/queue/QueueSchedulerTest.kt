package kr.hhplus.be.server.application.queue

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domain.queue.QueueState
import kr.hhplus.be.server.domain.queue.QueueStateRepository
import kr.hhplus.be.server.domain.queue.QueueToken

class QueueSchedulerTest : BehaviorSpec({

    val queueStateRepository = mockk<QueueStateRepository>(relaxed = true)

    lateinit var queueScheduler: QueueScheduler

    beforeTest {
        queueScheduler = QueueScheduler(queueStateRepository)
    }

    Given("QueueScheduler가 주기적으로 동작할 때") {
        val mockQueueState = mockk<QueueState>(relaxed = true)
        every { queueStateRepository.findAll() } returns listOf(mockQueueState)
        When("increaseEntranceNumber를 호출하면") {
            queueScheduler.increaseEntranceNumber()
            Then("queueRepository.incrementEntranceNumber가 amount= ${QueueToken.QUEUE_ENTRANCE_LIMIT}으로 호출된다") {
                verify { mockQueueState.incrementEntranceNumber(amount = QueueToken.QUEUE_ENTRANCE_LIMIT) }
            }
        }
    }
})