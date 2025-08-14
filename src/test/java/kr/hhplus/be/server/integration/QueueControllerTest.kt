package kr.hhplus.be.server.integration

import TestRedissonConfig
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.redis.testcontainers.RedisContainer
import kr.hhplus.be.server.application.queue.QueueTokenSigner
import kr.hhplus.be.server.domain.queue.QueueParticipantRepository
import kr.hhplus.be.server.domain.queue.QueueState
import kr.hhplus.be.server.domain.queue.QueueStateRepository
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


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestRedissonConfig::class)
class QueueControllerTest {

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
    lateinit var queueStateRepository: QueueStateRepository

    @Autowired
    lateinit var participantRepository: QueueParticipantRepository

    @Autowired
    lateinit var tokenSigner: QueueTokenSigner

    @Autowired
    lateinit var redissonClient: RedissonClient

    @BeforeEach
    fun setup() {
        participantRepository.deleteAll()
        queueStateRepository.deleteAll()
    }

    @Test
    fun `큐 토큰을 생성하고 상태를 확인할 수 있다`() {
        // given
        val accountId = "user-abc"
        val concertId = 1L
        val entranceNumber = 5L
        val totalParticipants = 0L

        queueStateRepository.save(
            QueueState(
                concertId = concertId,
                entranceNumber = entranceNumber,
                totalParticipantCount = totalParticipants
            )
        )

        // when: 토큰 발급
        val result = mockMvc.perform(
            post("/queue/token/$concertId")
                .header("X-ACCOUNT-ID", accountId)

        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").isString)
            .andReturn()
        val token = jacksonObjectMapper()
            .readValue<Map<String, String>>(result.response.contentAsString)["token"]!!
        mockMvc.perform(
            get("/queue/status")
                .header("X-ACCOUNT-ID", accountId)
                .header("X-QUEUE-TOKEN-ID", token)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.queueNumber").value(1))
            .andExpect(jsonPath("$.isAllowedToEnter").value(true))
            .andExpect(jsonPath("$.estimateWaitTime").isNumber)
    }
}
