package kr.hhplus.be.server.application.queue

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.common.exception.AlreadyAssignedQueueAccountException
import kr.hhplus.be.server.domain.queue.QueueRepository
import org.junit.jupiter.api.assertThrows

class CreateTokenServiceTest : BehaviorSpec({

    lateinit var createTokenService: CreateTokenService

    val queueRepository = mockk<QueueRepository>(relaxed = true)
    val queueTokenSigner = mockk<QueueTokenSigner>()

    beforeTest {
        createTokenService = CreateTokenService(queueRepository, queueTokenSigner)
    }

    Given("대기열 토큰 생성에서") {
        every { queueRepository.assignQueueNumber(any()) } returns null
        When("이미 큐에 존재하는 유저가") {
            Then("AlreadyAssignedQueueAccountException이 발생한다") {
                assertThrows<AlreadyAssignedQueueAccountException> { createTokenService.execute("account") }
            }
        }
    }
})
