package kr.hhplus.be.server.application.queue

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.common.exception.AlreadyAssignedQueueAccountException
import kr.hhplus.be.server.common.exception.QueueNotFoundException
import kr.hhplus.be.server.domain.queue.QueueRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

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

    Given("사용자가 유효한 토큰을 갖고 있고, 큐 번호도 존재하며 입장 가능한 경우") {
        val queueNumber = 1L
        val tokenId = "encoded-token"
        val decodedToken =
            QueueToken(accountId = "accountId", queueNumber = queueNumber, createdAt = LocalDateTime.now())
        every { queueTokenSigner.decode(any()) } returns decodedToken
        every { queueRepository.getQueueNumber(any()) } returns queueNumber
        every { queueRepository.getCurrentEntranceNumber() } returns 10L

        When("getStatus를 호출하면") {
            val result = queueService.getStatus(tokenId)

            Then("큐 번호와 입장 여부, 예상 대기시간이 포함된 결과가 반환된다") {
                result.queueNumber shouldBe queueNumber
                result.isAllowedToEnter.shouldBeTrue()
                result.estimateWaitTime shouldBeGreaterThanOrEqual 0L
            }
        }
    }

    Given("큐 번호가 존재하지 않는 경우") {
        val tokenId = "encoded-token"
        val decodedToken = QueueToken(accountId = "accountId", queueNumber = 1L, createdAt = LocalDateTime.now())
        every { queueTokenSigner.decode(tokenId) } returns decodedToken
        every { queueRepository.getQueueNumber(any()) } returns null

        When("getStatus를 호출하면") {
            Then("QueueNotFoundException이 발생한다") {
                shouldThrow<QueueNotFoundException> {
                    queueService.getStatus(tokenId)
                }
            }
        }
    }

    Given("현재 입장 번호보다 너무 오래된 큐 번호인 경우 (만료)") {
        val tokenId = "encoded-token"
        val decodedToken = QueueToken(accountId = "accountId", queueNumber = 1L, createdAt = LocalDateTime.now())
        every { queueTokenSigner.decode(any()) } returns decodedToken
        every { queueRepository.getQueueNumber(any()) } returns 10L
        every { queueRepository.getCurrentEntranceNumber() } returns 1000000L

        When("getStatus를 호출하면") {
            val result = queueService.getStatus(tokenId)

            Then("입장 불가 상태가 된다") {
                result.isAllowedToEnter.shouldBeFalse()
                result.estimateWaitTime shouldBeGreaterThanOrEqual 0L
            }
        }
    }
})
