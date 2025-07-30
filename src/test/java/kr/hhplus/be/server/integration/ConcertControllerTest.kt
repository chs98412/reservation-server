package kr.hhplus.be.server.integration

import kr.hhplus.be.server.application.queue.QueueTokenSigner
import kr.hhplus.be.server.domain.concert.*
import kr.hhplus.be.server.domain.queue.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@EnableJpaRepositories(basePackages = ["kr.hhplus.be.server.domain"])
@EntityScan(basePackages = ["kr.hhplus.be.server.domain"])
class ConcertControllerTest {

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

    @BeforeEach
    fun setup() {


        // DB 초기화
        participantRepository.deleteAll()
        queueStateRepository.deleteAll()
        reservationRepository.deleteAll()
        concertRepository.deleteAll()

        // 콘서트 + 예약 데이터 삽입
        val concertId = 1L
        concertRepository.save(
            Concert(
                name = "테스트",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(3)
            )
        )

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
    }


    @Test
    fun `입장 가능한 유저는 예약 가능한 날짜를 조회할 수 있다`() {
        // given
        val concertId = 1L
        val token = QueueToken.create("user-123", concertId).let { queueTokenSigner.encode(it) }

        // when & then
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

}
