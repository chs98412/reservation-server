package kr.hhplus.be.server.integration

import io.kotest.matchers.shouldBe
import kr.hhplus.be.server.application.queue.CreateTokenService
import kr.hhplus.be.server.domain.queue.QueueParticipantRepository
import kr.hhplus.be.server.domain.queue.QueueState
import kr.hhplus.be.server.domain.queue.QueueStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class CreateTokenConcurrencyTest {

    @Autowired
    private lateinit var createTokenService: CreateTokenService

    @Autowired
    private lateinit var participantRepository: QueueParticipantRepository

    @Autowired
    private lateinit var queueStateRepository: QueueStateRepository

    @BeforeEach
    fun setup() {
        participantRepository.deleteAll()
        queueStateRepository.deleteAll()
        queueStateRepository.save(QueueState(concertId = 1L))
    }

    @Test
    fun `여러 사용자가 동시에 토큰을 발급받으면 totalParticipantCount는 10이어야 한다`() {
        // given
        val concertId = 1L
        val accountsCount = 1000L
        val accountIds = (1..accountsCount).map { "account$it" }

        val executor = Executors.newFixedThreadPool(accountIds.size)
        val latch = CountDownLatch(accountIds.size)
        val start = System.currentTimeMillis()

        accountIds.forEach { accId ->
            executor.submit {
                try {
                    createTokenService.execute(accId, concertId)
                } catch (e: Exception) {
                    println("예외: ${e.message}")
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        val end = System.currentTimeMillis()
        val duration = end - start
        println("🔧 전체 실행 시간: ${duration}ms")

        val queueState = queueStateRepository.findAll().first { it.concertId == concertId }
        println("총 대기 인원 수: ${queueState?.totalParticipantCount}")
        queueState?.totalParticipantCount shouldBe accountsCount
    }
}
