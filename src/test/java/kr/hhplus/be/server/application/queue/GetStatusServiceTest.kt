package kr.hhplus.be.server.application.queue

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.common.exception.AccountNotFoundInQueueException
import kr.hhplus.be.server.domain.queue.*
import java.time.LocalDateTime

class GetStatusServiceTest : BehaviorSpec({

    lateinit var getStatusService: GetStatusService

    val queueTokenSigner = mockk<QueueTokenSigner>()
    val participantRepository = mockk<QueueParticipantRepository>()
    val queueStateRepository = mockk<QueueStateRepository>()

    beforeTest {
        getStatusService = GetStatusService(queueTokenSigner, participantRepository, queueStateRepository)
    }

    Given("사용자가 유효한 토큰을 갖고 있고, 큐 번호도 존재하며 입장 가능한 경우") {
        val queueNumber = 1L
        val tokenId = "encoded-token"
        val decodedToken =
            QueueToken(accountId = "accountId", queueNumber = queueNumber, createdAt = LocalDateTime.now())
        every { queueTokenSigner.decode(any()) } returns decodedToken
        every { participantRepository.findByAccountId(any()) } returns QueueParticipant(
            accountId = "accountId",
            queueNumber = queueNumber,
        )
        every { queueStateRepository.findByConcertId(concertId = 1L) } returns QueueState(entranceNumber = 10L)

        When("getStatus를 호출하면") {
            val result = getStatusService.execute(tokenId)

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
        every { participantRepository.findByAccountId(any()) } returns null

        When("getStatus를 호출하면") {
            Then("QueueNotFoundException이 발생한다") {
                shouldThrow<AccountNotFoundInQueueException> {
                    getStatusService.execute(tokenId)
                }
            }
        }
    }

    Given("현재 입장 번호보다 너무 오래된 큐 번호인 경우 (만료)") {
        val tokenId = "encoded-token"
        val decodedToken = QueueToken(accountId = "accountId", queueNumber = 1L, createdAt = LocalDateTime.now())
        every { queueTokenSigner.decode(any()) } returns decodedToken
        every { participantRepository.findByAccountId(any()) } returns QueueParticipant(
            accountId = "accountId",
            queueNumber = 1L
        )
        every { queueStateRepository.findByConcertId(concertId = 1L) } returns QueueState(entranceNumber = 1000000L)

        When("getStatus를 호출하면") {
            val result = getStatusService.execute(tokenId)

            Then("입장 불가 상태가 된다") {
                result.isAllowedToEnter.shouldBeFalse()
                result.estimateWaitTime shouldBeGreaterThanOrEqual 0L
            }
        }
    }
})