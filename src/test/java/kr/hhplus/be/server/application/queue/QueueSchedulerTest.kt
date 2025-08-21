package kr.hhplus.be.server.application.queue

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import java.time.LocalDate

class QueueSchedulerTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerTest

    val concertRepository = mockk<ConcertRepository>(relaxed = true)
    val queueCacheRepository = mockk<QueueCacheRepository>(relaxed = true)

    lateinit var queueScheduler: QueueScheduler

    beforeTest {
        queueScheduler = QueueScheduler(concertRepository, queueCacheRepository)
    }

    Given("대기열에 유저가 있는 콘서트가 있을 때") {
        val concert = Concert(
            id = 1L,
            name = "테스트콘서트",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
        )

        every { concertRepository.findAll() } returns listOf(concert)
        every { queueCacheRepository.pollFirstWaiting(concert.id) } returnsMany listOf("user1", "user2", null)

        When("increaseEntranceNumber를 호출하면") {
            queueScheduler.increaseEntranceNumber()

            Then("QUEUE_ENTRANCE_LIMIT 만큼 poll과 addActive가 호출된다") {
                verify(exactly = 2) { queueCacheRepository.addActive(any(), any(), any()) }
                verify(atLeast = 1) { queueCacheRepository.pollFirstWaiting(any()) }
            }
        }
    }

    Given("대기열이 비어있는 경우") {
        val concert = Concert(
            id = 2L,
            name = "비어있는콘서트",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
        )

        every { concertRepository.findAll() } returns listOf(concert)
        every { queueCacheRepository.pollFirstWaiting(concert.id) } returns null

        When("increaseEntranceNumber를 호출하면") {
            queueScheduler.increaseEntranceNumber()

            Then("addActive는 호출되지 않는다") {
                verify(exactly = 0) { queueCacheRepository.addActive(any(), any(), any()) }
            }
        }
    }
})
