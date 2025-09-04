package kr.hhplus.be.server.application.queue

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.application.kafka.KafkaEventPublisher
import kr.hhplus.be.server.common.exception.AlreadyAssignedQueueAccountException
import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import org.junit.jupiter.api.assertThrows

class CreateTokenServiceTest : BehaviorSpec({

    lateinit var createTokenService: CreateTokenService

    val queueTokenSigner = mockk<QueueTokenSigner>()
    val queueCacheRepository = mockk<QueueCacheRepository>(relaxed = true)
    val kafkaEventPublisher = mockk<KafkaEventPublisher>(relaxed = true)

    beforeTest {
        createTokenService =
            CreateTokenService(queueTokenSigner, queueCacheRepository, kafkaEventPublisher)
    }

    Given("대기열 토큰 생성에서") {
        every { queueCacheRepository.existsInActive(any(), any()) } returns true
        When("이미 큐에 존재하는 유저가") {
            Then("AlreadyAssignedQueueAccountException이 발생한다") {
                assertThrows<AlreadyAssignedQueueAccountException> { createTokenService.execute("account", 1) }
            }
        }
    }
})
