package kr.hhplus.be.server.integration

import TestRedissonConfig
import com.ninjasquad.springmockk.MockkBean
import com.redis.testcontainers.RedisContainer
import io.mockk.coEvery
import kr.hhplus.be.server.application.queue.GetStatusUseCase
import kr.hhplus.be.server.application.queue.QueueStatusResponse
import kr.hhplus.be.server.application.queue.QueueTokenSigner
import kr.hhplus.be.server.domain.concert.*
import kr.hhplus.be.server.domain.queue.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
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
class ConcertControllerTest {

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
    lateinit var participantRepository: QueueParticipantRepository

    @Autowired
    lateinit var queueStateRepository: QueueStateRepository

    @Autowired
    lateinit var concertRepository: ConcertRepository

    @Autowired
    lateinit var reservationRepository: ReservationRepository

    @Autowired
    lateinit var queueTokenSigner: QueueTokenSigner

    @Autowired
    lateinit var redissonClient: RedissonClient

    @MockkBean(relaxed = true)
    lateinit var getStatusUseCase: GetStatusUseCase

    @BeforeEach
    fun setup() {
        participantRepository.deleteAll()
        queueStateRepository.deleteAll()
        reservationRepository.deleteAll()
        concertRepository.deleteAll()
        coEvery { getStatusUseCase.execute(any()) } returns QueueStatusResponse(true)
    }


    @Test
    fun `입장 가능한 유저는 예약 가능한 날짜를 조회할 수 있다`() {
        val concertId = concertRepository.save(
            Concert(
                name = "테스트",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(3)
            )
        ).id

        reservationRepository.saveAll(
            listOf(
                Reservation(
                    concertId = concertId,
                    seatNo = 1,
                    date = LocalDate.now().plusDays(1),
                    status = Status.AVAILABLE
                ),
                Reservation(
                    concertId = concertId,
                    seatNo = 2,
                    date = LocalDate.now().plusDays(2),
                    status = Status.AVAILABLE
                ),
                Reservation(
                    concertId = concertId,
                    seatNo = 3,
                    date = LocalDate.now().plusDays(1),
                    status = Status.RESERVED
                )
            )
        )

        queueStateRepository.save(QueueState(concertId = concertId, entranceNumber = 5, totalParticipantCount = 10))
        participantRepository.save(QueueParticipant(accountId = "user-123", queueNumber = 5))
        val token = QueueToken.create("user-123", concertId).let { queueTokenSigner.encode(it) }

        mockMvc.perform(
            get("/reservation/available-dates")
                .header("X-ACCOUNT-ID", "user-123")
                .header("X-QUEUE-TOKEN-ID", token)
                .param("concert-id", concertId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.availableDates").isArray)
            .andExpect(jsonPath("$.availableDates.length()").value(2))
    }

    @Test
    fun `입장 가능한 유저는 특정 날짜의 예약 가능한 좌석을 조회할 수 있다`() {
        val concertId = concertRepository.save(
            Concert(
                name = "테스트 콘서트",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(5)
            )
        ).id
        val targetDate = LocalDate.now().plusDays(1)



        reservationRepository.saveAll(
            listOf(
                Reservation(
                    concertId = concertId,
                    seatNo = 101,
                    date = targetDate,
                    status = Status.AVAILABLE
                ),
                Reservation(
                    concertId = concertId,
                    seatNo = 102,
                    date = targetDate,
                    status = Status.RESERVED
                ),
                Reservation(
                    concertId = concertId,
                    seatNo = 103,
                    date = targetDate.plusDays(1),
                    status = Status.AVAILABLE
                )
            )
        )

        queueStateRepository.save(QueueState(concertId = concertId, entranceNumber = 3, totalParticipantCount = 5))
        participantRepository.save(QueueParticipant(accountId = "user-456", queueNumber = 3))
        val token = QueueToken.create("user-456", concertId).let { queueTokenSigner.encode(it) }

        mockMvc.perform(
            get("/reservation/available-seats")
                .header("X-ACCOUNT-ID", "user-456")
                .header("X-QUEUE-TOKEN-ID", token)
                .param("concert-id", concertId.toString())
                .param("date", targetDate.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.availableReservationIdList").isArray)
            .andExpect(jsonPath("$.availableReservationIdList.length()").value(1))
    }

    @Test
    fun `입장 가능한 유저는 좌석 예약에 성공할 수 있다`() {
        val concertId = concertRepository.save(
            Concert(
                name = "좌석예약콘서트",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(1)
            )
        ).id
        val seatNo = 10
        val accountId = "user-789"



        reservationRepository.save(
            Reservation(
                concertId = concertId,
                seatNo = seatNo,
                date = LocalDate.now(),
                status = Status.AVAILABLE
            )
        )

        queueStateRepository.save(QueueState(concertId = concertId, entranceNumber = 2, totalParticipantCount = 10))
        participantRepository.save(QueueParticipant(accountId = accountId, queueNumber = 2))
        val token = QueueToken.create(accountId, concertId).let { queueTokenSigner.encode(it) }

        val requestBody = """
        {
            "concertId": $concertId,
            "seatNo": $seatNo
        }
    """.trimIndent()

        mockMvc.perform(
            post("/reservation")
                .header("X-ACCOUNT-ID", accountId)
                .header("X-QUEUE-TOKEN-ID", token)
                .contentType("application/json")
                .content(requestBody)
        )
            .andExpect(status().isNoContent)
    }

}
