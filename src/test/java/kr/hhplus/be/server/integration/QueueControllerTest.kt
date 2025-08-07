package kr.hhplus.be.server.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kr.hhplus.be.server.application.queue.QueueTokenSigner
import kr.hhplus.be.server.domain.queue.QueueParticipantRepository
import kr.hhplus.be.server.domain.queue.QueueState
import kr.hhplus.be.server.domain.queue.QueueStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@EnableJpaRepositories(basePackages = ["kr.hhplus.be.server.domain"])
@EntityScan(basePackages = ["kr.hhplus.be.server.domain"])
class QueueControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var queueStateRepository: QueueStateRepository

    @Autowired
    lateinit var participantRepository: QueueParticipantRepository

    @Autowired
    lateinit var tokenSigner: QueueTokenSigner

    @BeforeEach
    fun setup() {
        participantRepository.deleteAll()
        queueStateRepository.deleteAll()
    }

    @Test
    fun `큐 토큰을 생성하고 상태를 확인할 수 있다`() {
        // given
        val accountId = "user-abc"
        val concertId = 999L
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
