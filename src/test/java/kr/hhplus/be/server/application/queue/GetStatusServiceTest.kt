package kr.hhplus.be.server.application.queue

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.common.exception.AccountNotFoundInQueueException
import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import java.time.LocalDateTime

class GetStatusServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerTest

    val queueTokenSigner = mockk<QueueTokenSigner>()
    val queueCacheRepository = mockk<QueueCacheRepository>()

    val getStatusService = GetStatusService(queueTokenSigner, queueCacheRepository)

    Given("사용자가 유효한 토큰을 갖고 있고 active 상태인 경우") {
        val queueNumber = 1L
        val tokenId = "encoded-token"
        val decodedToken = QueueToken(
            accountId = "accountId",
            queueNumber = queueNumber,
            createdAt = LocalDateTime.now()
        )

        every { queueTokenSigner.decode(tokenId) } returns decodedToken
        every { queueCacheRepository.existsInActive(any(), any()) } returns true

        When("getStatus를 호출하면") {
            val result = getStatusService.execute(tokenId)

            Then("입장 가능 상태가 반환된다") {
                result.isAllowedToEnter.shouldBeTrue()
            }
        }
    }

    Given("사용자가 대기열에 있지만 active가 아닌 경우") {
        val tokenId = "encoded-token"
        val decodedToken = QueueToken(
            accountId = "accountId",
            queueNumber = 5L,
            createdAt = LocalDateTime.now()
        )

        every { queueTokenSigner.decode(tokenId) } returns decodedToken
        every { queueCacheRepository.existsInActive(any(), any()) } returns false

        When("getStatus를 호출하면") {
            val result = getStatusService.execute(tokenId)

            Then("입장 불가 상태와 예상 대기 시간이 계산된다") {
                result.isAllowedToEnter.shouldBeFalse()
            }
        }
    }

    Given("대기열에서 사용자를 찾을 수 없는 경우") {
        val tokenId = "encoded-token"
        val decodedToken = QueueToken(
            accountId = "accountId",
            queueNumber = 10L,
            createdAt = LocalDateTime.now()
        )

        every { queueTokenSigner.decode(tokenId) } returns decodedToken
        every { queueCacheRepository.existsInActive(any(), any()) } returns false

        When("getStatus를 호출하면") {
            Then("AccountNotFoundInQueueException 이 발생한다") {
                shouldThrow<AccountNotFoundInQueueException> {
                    getStatusService.execute(tokenId)
                }
            }
        }
    }
})
