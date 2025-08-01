package kr.hhplus.be.server.application.queue

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.common.exception.AlreadyAssignedQueueAccountException
import kr.hhplus.be.server.domain.queue.QueueParticipantRepository
import kr.hhplus.be.server.domain.queue.QueueStateRepository
import org.junit.jupiter.api.assertThrows

class CreateTokenServiceTest : BehaviorSpec({

    lateinit var createTokenService: CreateTokenService

    val queueTokenSigner = mockk<QueueTokenSigner>()
    val participantRepository = mockk<QueueParticipantRepository>(relaxed = true)
    val queueStateRepository = mockk<QueueStateRepository>(relaxed = true)

    beforeTest {
        createTokenService = CreateTokenService(queueTokenSigner, participantRepository, queueStateRepository)
    }

    Given("대기열 토큰 생성에서") {
        every { participantRepository.existsByAccountId(any()) } returns true
        When("이미 큐에 존재하는 유저가") {
            Then("AlreadyAssignedQueueAccountException이 발생한다") {
                assertThrows<AlreadyAssignedQueueAccountException> { createTokenService.execute("account", 1) }
            }
        }
    }
})
