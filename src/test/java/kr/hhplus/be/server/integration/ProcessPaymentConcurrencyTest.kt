package kr.hhplus.be.server.integration

import TestRedissonConfig
import com.redis.testcontainers.RedisContainer
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import kr.hhplus.be.server.application.point.ProcessPaymentService
import kr.hhplus.be.server.domain.concert.Reservation
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.concert.Status
import kr.hhplus.be.server.domain.point.Balance
import kr.hhplus.be.server.domain.point.BalanceRepository
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
class ProcessPaymentConcurrencyTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var processPaymentService: ProcessPaymentService

    @Autowired
    private lateinit var balanceRepository: BalanceRepository

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    private val accountId = "test-user"

    @BeforeEach
    fun setUp() {
        reservationRepository.deleteAll()
        balanceRepository.deleteAll()

        balanceRepository.save(Balance(accountId = accountId, amount = 10_000))

        reservationRepository.saveAll(
            listOf(
                Reservation(
                    concertId = 1L,
                    seatNo = 1,
                    status = Status.RESERVED,
                    accountId = accountId,
                    price = 3_000,
                    date = LocalDate.now()
                ),
                Reservation(
                    concertId = 1L,
                    seatNo = 2,
                    status = Status.RESERVED,
                    accountId = accountId,
                    price = 3_000,
                    date = LocalDate.now()
                ),
            )
        )
    }

    @Test
    fun `동시에 결제를 요청하면 하나만 성공하고 나머지는 실패해야 한다`() {
        val threadCount = 5
        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        val successList = Collections.synchronizedList(mutableListOf<Int>())
        val failedList = Collections.synchronizedList(mutableListOf<Int>())

        repeat(threadCount) { idx ->
            executor.submit {
                try {
                    val result = pay()
                    val ex = result.resolvedException

                    if (ex is LockAcquireException) {
                        failedList.add(idx)
                    } else {
                        successList.add(idx)
                    }
                } catch (e: Exception) {
                    failedList.add(idx)
                    throw e
                } finally {
                    latch.countDown()
                }
            }
        }


        latch.await()
        val balance = balanceRepository.findByAccountId(accountId)!!

        assertSoftly {
            successList.size shouldBe 1
            failedList.size shouldBe threadCount - 1
            balance.amount shouldBe 4000
        }

    }

    fun pay(): MvcResult {
        return mockMvc.perform(
            post("/point/payment")
                .header("X-ACCOUNT-ID", accountId)
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
