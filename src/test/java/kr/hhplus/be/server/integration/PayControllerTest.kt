package kr.hhplus.be.server.integration

import TestRedissonConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.redis.testcontainers.RedisContainer
import kr.hhplus.be.server.application.queue.QueueTokenSigner
import kr.hhplus.be.server.domain.concert.*
import kr.hhplus.be.server.domain.point.Balance
import kr.hhplus.be.server.domain.point.BalanceRepository
import kr.hhplus.be.server.domain.queue.QueueParticipantRepository
import kr.hhplus.be.server.domain.queue.QueueStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestRedissonConfig::class)
class PayControllerTest {

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

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var balanceRepository: BalanceRepository

    @Autowired
    lateinit var reservationRepository: ReservationRepository

    @Autowired
    lateinit var concertRepository: ConcertRepository

    @Autowired
    lateinit var queueStateRepository: QueueStateRepository

    @Autowired
    lateinit var participantRepository: QueueParticipantRepository

    @Autowired
    lateinit var queueTokenSigner: QueueTokenSigner

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var redissonClient: RedissonClient

    private val accountId = "user-100"

    @BeforeEach
    fun setup() {
        reservationRepository.deleteAll()
        concertRepository.deleteAll()
        balanceRepository.deleteAll()
        queueStateRepository.deleteAll()
        participantRepository.deleteAll()
    }

    @Test
    fun `포인트 충전 후 조회 시 반영된다`() {
        balanceRepository.save(Balance(accountId = accountId, amount = 10_000))

        mockMvc.perform(
            post("/point/charge")
                .header("X-ACCOUNT-ID", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"amount": 3000}""")
        ).andExpect(status().isNoContent)

        mockMvc.perform(
            get("/point")
                .header("X-ACCOUNT-ID", accountId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.point").value(13_000))
    }

    @Test
    fun `예약된 좌석 결제 후 포인트 차감 및 상태 변경`() {
        balanceRepository.save(Balance(accountId = accountId, amount = 10_000))

        val concert = concertRepository.save(
            Concert(name = "테스트콘서트", startDate = LocalDate.now(), endDate = LocalDate.now().plusDays(2))
        )

        reservationRepository.saveAll(
            listOf(
                Reservation(
                    concertId = concert.id,
                    seatNo = 1,
                    date = LocalDate.now(),
                    status = Status.RESERVED,
                    accountId = accountId,
                    price = 1000L
                ),
                Reservation(
                    concertId = concert.id,
                    seatNo = 2,
                    date = LocalDate.now(),
                    status = Status.RESERVED,
                    accountId = accountId,
                    price = 2000L
                )
            )
        )

        mockMvc.perform(
            post("/point/payment")
                .header("X-ACCOUNT-ID", accountId)
        ).andExpect(status().isNoContent)

        mockMvc.perform(
            get("/point")
                .header("X-ACCOUNT-ID", accountId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.point").value(7000))

        val updated = reservationRepository.findAllByAccountIdAndStatus(accountId, Status.PAID)
        assert(updated.size == 2)
    }
}
