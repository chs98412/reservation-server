package kr.hhplus.be.server.application.queue

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import kr.hhplus.be.server.eventListener.concert.QueueEventListener
import kr.hhplus.be.server.eventListener.concert.model.QueueJoinEvent

class QueueSchedulerTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerTest
    val objectMapper = jacksonObjectMapper()

    val queueCacheRepository = mockk<QueueCacheRepository>(relaxed = true)

    lateinit var queueEventListener: QueueEventListener

    beforeTest {
        queueEventListener = QueueEventListener(queueCacheRepository)
    }

    Given("대기열에 유저가 있는 콘서트가 있을 때") {
        val event1 = objectMapper.writeValueAsString(QueueJoinEvent(1, "accountId1"))
        val event2 = objectMapper.writeValueAsString(QueueJoinEvent(2, "accountId2"))
        val event3 = objectMapper.writeValueAsString(QueueJoinEvent(3, "accountId3"))

        When("increaseEntranceNumber를 호출하면") {
            queueEventListener.increaseEntranceNumber(listOf(event1, event2, event3))

            Then("addActive가 호출된다") {
                verify { queueCacheRepository.addActive(any(), any(), any()) }
            }
        }
    }
})
