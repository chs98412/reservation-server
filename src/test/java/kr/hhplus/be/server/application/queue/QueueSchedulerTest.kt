package kr.hhplus.be.server.application.queue

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domain.queue.QueueRepository
import kr.hhplus.be.server.domain.queue.QueueToken

class QueueSchedulerTest : BehaviorSpec({

    val queueRepository = mockk<QueueRepository>(relaxed = true)

    lateinit var queueScheduler: QueueScheduler

    beforeTest {
        queueScheduler = QueueScheduler(queueRepository)
    }

    Given("QueueScheduler가 주기적으로 동작할 때") {
        When("increaseEntranceNumber를 호출하면") {
            queueScheduler.increaseEntranceNumber()

            Then("queueRepository.incrementEntranceNumber가 amount= ${QueueToken.QUEUE_ENTRANCE_LIMIT}으로 1번 호출된다") {
                verify(exactly = 1) { queueRepository.incrementEntranceNumber(amount = QueueToken.QUEUE_ENTRANCE_LIMIT) }
            }
        }
    }
})