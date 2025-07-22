package kr.hhplus.be.server.application

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.AlreadyAssignedQueueAccountException
import kr.hhplus.be.server.domain.queue.QueueRepository
import org.junit.jupiter.api.assertThrows

class QueueServiceTest : BehaviorSpec({

    lateinit var queueService: QueueService

    val queueRepository = mockk<QueueRepository>(relaxed = true)
    val queueTokenSigner = mockk<QueueTokenSigner>()

    beforeTest {
        queueService = QueueService(queueRepository, queueTokenSigner)
    }

    Given("대기열 토큰 생성에서") {
        every { queueRepository.assignQueueNumber(any()) } returns null
        When("이미 큐에 존재하는 유저가") {
            Then("AlreadyAssignedQueueAccountException이 발생한다") {
                assertThrows<AlreadyAssignedQueueAccountException> { queueService.createToken("account") }
            }
        }
    }
})
