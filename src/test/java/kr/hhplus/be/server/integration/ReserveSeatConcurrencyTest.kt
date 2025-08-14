package kr.hhplus.be.server.integration

import TestRedissonConfig
import com.ninjasquad.springmockk.MockkBean
import com.redis.testcontainers.RedisContainer
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import kr.hhplus.be.server.application.concert.ReserveSeatService
import kr.hhplus.be.server.application.queue.GetStatusUseCase
import kr.hhplus.be.server.application.queue.QueueStatusResponse
import kr.hhplus.be.server.domain.concert.Reservation
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.concert.Status
import kr.hhplus.be.server.infrastructure.LockAcquireException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors


@SpringBootTest
@Import(TestRedissonConfig::class)
@AutoConfigureMockMvc
class ReserveSeatConcurrencyTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var reserveSeatService: ReserveSeatService

    @Autowired
    lateinit var reservationRepository: ReservationRepository

    @MockkBean(relaxed = true)
    lateinit var getStatusUseCase: GetStatusUseCase

    @BeforeEach
    fun setUp() {
        reservationRepository.deleteAll()
        coEvery { getStatusUseCase.execute(any()) } returns QueueStatusResponse(1L, true, 0L)
    }

    @Test
    fun `동시에 같은 좌석을 예약하면 하나만 성공하고 나머지는 실패해야 한다`() {
        val concertId = 1L
        val seatNo = 1
        val accountIds = listOf("accountId1", "accountId2", "accountId3", "accountId4", "accountId5")

        reservationRepository.save(
            Reservation(
                concertId = concertId,
                seatNo = seatNo,
                status = Status.AVAILABLE,
                accountId = null,
                date = LocalDate.now()
            )
        )

        val executor = Executors.newFixedThreadPool(accountIds.size)
        val latch = CountDownLatch(accountIds.size)
        val succeedResults = Collections.synchronizedList(mutableListOf<String>())
        val failedResults = Collections.synchronizedList(mutableListOf<String>())

        accountIds.forEach { accId ->
            executor.submit {
                try {
                    val result = reserve(accId)
                    val ex = result.resolvedException

                    if (ex is LockAcquireException) {
                        failedResults.add(accId)
                    } else {
                        succeedResults.add(accId)
                    }
                } catch (e: Exception) {
                    failedResults.add(accId)
                    throw e
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        assertSoftly {
            succeedResults.size shouldBe 1
            failedResults.size shouldBe accountIds.size - 1
        }
    }

    fun reserve(accountId: String): MvcResult {
        val requestBody = """
        {
            "concertId": 1,
            "seatNo": 1
        }
    """.trimIndent()

        return mockMvc.perform(
            post("/reservation")
                .header("X-ACCOUNT-ID", accountId)
                .header("X-QUEUE-TOKEN-ID", "token")
                .contentType("application/json")
                .content(requestBody)
        ).andReturn()
    }


    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val redis: RedisContainer = RedisContainer(
            DockerImageName.parse("redis:7.2-alpine")
        ).apply {
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.redis.host") { redis.host }
            registry.add("spring.redis.port") { redis.firstMappedPort }
        }

    }
}
