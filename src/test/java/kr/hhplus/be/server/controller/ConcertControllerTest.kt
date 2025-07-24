package kr.hhplus.be.server.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.headerWithName
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import kr.hhplus.be.server.application.concert.*
import kr.hhplus.be.server.application.queue.GetStatusUseCase
import kr.hhplus.be.server.application.queue.QueueStatusResponse
import kr.hhplus.be.server.presentation.ConcertController
import kr.hhplus.be.server.presentation.model.SeatReservationRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.snippet.Attributes
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate

@TestConfiguration
class ReservationMockConfig {
    @Bean
    fun getStatusUseCase(): GetStatusUseCase = mockk(relaxed = true)

    @Bean
    fun getAvailableDatesUseCase(): GetAvailableDatesUseCase = mockk(relaxed = true)

    @Bean
    fun getAvailableSeatsUseCase(): GetAvailableSeatsUseCase = mockk(relaxed = true)

    @Bean
    fun reserveSeatUseCase(): ReserveSeatUseCase = mockk(relaxed = true)
}

@ExtendWith(RestDocumentationExtension::class)
@Import(ReservationMockConfig::class)
@WebMvcTest(controllers = [ConcertController::class])
class ConcertControllerTest {
    @Autowired
    lateinit var context: WebApplicationContext

    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var getStatusUseCase: GetStatusUseCase

    @Autowired
    lateinit var getAvailableDatesUseCase: GetAvailableDatesUseCase

    @Autowired
    lateinit var getAvailableSeatsUseCase: GetAvailableSeatsUseCase

    @Autowired
    lateinit var reserveSeatUseCase: ReserveSeatUseCase

    val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
            )
            .build()
    }

    @Test
    fun `예약 가능 날짜 조회 API`() {
        val queueSummary = QueueStatusResponse(queueNumber = 10, isAllowedToEnter = true, estimateWaitTime = 1000)
        every { getStatusUseCase.execute(any()) } returns queueSummary

        val summary = ReservationAvailableDatesResponse(availableDates = listOf(LocalDate.now()))
        every { getAvailableDatesUseCase.execute(any()) } returns summary
        mockMvc.perform(
            get("/reservation/available-dates?concert-id={concert-id}", "concert_a")
                .header("X-ACCOUNT-ID", "account123")
                .header("X-QUEUE-TOKEN-ID", "bb7de087-2e5d-4b6c-b7c4-bb3b97360d24")
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-available-dates",
                    preprocessResponse(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("예약 가능 날짜 조회")
                            .requestHeaders(
                                headerWithName("X-ACCOUNT-ID").description("사용자 식별 헤더"),
                                headerWithName("X-QUEUE-TOKEN-ID").description("대기열 토큰 헤더")
                            )
                            .queryParameters(
                                parameterWithName("concert-id").description("콘서트 ID")
                            )
                            .responseFields(
                                fieldWithPath("availableDates").type(JsonFieldType.ARRAY).description("예약 가능 날짜 목록")
                                    .attributes(
                                        Attributes.key("availableDates")
                                            .value(summary.availableDates.map { it.toString() })
                                    ),
                            )
                            .build()
                    )
                )
            )
    }


    @Test
    fun `예약 가능 좌석 조회 API`() {
        val queueSummary = QueueStatusResponse(queueNumber = 10, isAllowedToEnter = true, estimateWaitTime = 1000)
        every { getStatusUseCase.execute(any()) } returns queueSummary

        val summary = AvailableConcertReservationFetchResponse(availableConcertIdList = listOf(1, 2, 3))
        every { getAvailableSeatsUseCase.execute(any(), any()) } returns summary
        mockMvc.perform(
            get("/reservation/available-seats?date={date}&concert-id={concert-id}", LocalDate.now(), "concert_a")
                .header("X-ACCOUNT-ID", "account123")
                .header("X-QUEUE-TOKEN-ID", "bb7de087-2e5d-4b6c-b7c4-bb3b97360d24")
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-available-seats",
                    preprocessResponse(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("예약 가능 좌석 조회")
                            .requestHeaders(
                                headerWithName("X-ACCOUNT-ID").description("사용자 식별 헤더"),
                                headerWithName("X-QUEUE-TOKEN-ID").description("대기열 토큰 헤더")
                            )
                            .queryParameters(
                                parameterWithName("date").description("검색 일자"),
                                parameterWithName("concert-id").description("콘서트 식별자")
                            )
                            .responseFields(
                                fieldWithPath("availableConcertIdList").type(JsonFieldType.ARRAY)
                                    .description("예약 가능 좌석 목록")
                                    .attributes(
                                        Attributes.key("availableConcertIdList").value(summary.availableConcertIdList)
                                    ),
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    fun `좌석 예약 요청 API`() {
        val queueSummary = QueueStatusResponse(queueNumber = 10, isAllowedToEnter = true, estimateWaitTime = 1000)
        every { getStatusUseCase.execute(any()) } returns queueSummary
        val requestBody = SeatReservationRequest(concertId = "concert-id", scheduleId = 1, seatNo = 1)
        justRun { reserveSeatUseCase.execute(any()) }
        mockMvc.perform(
            post("/reservation")
                .header("X-ACCOUNT-ID", "account123")
                .header("X-QUEUE-TOKEN-ID", "bb7de087-2e5d-4b6c-b7c4-bb3b97360d24")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isNoContent)
            .andDo(
                document(
                    "reserve-seat",
                    preprocessResponse(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("좌석 예약 요청")
                            .requestHeaders(
                                headerWithName("X-ACCOUNT-ID").description("사용자 식별 헤더"),
                                headerWithName("X-QUEUE-TOKEN-ID").description("대기열 토큰 헤더")
                            )
                            .requestFields(
                                fieldWithPath("concertId").type(JsonFieldType.STRING).description("콘서트 ID").attributes(
                                    Attributes.key("concertId").value(requestBody.concertId)
                                ),
                                fieldWithPath("scheduleId").type(JsonFieldType.NUMBER).description("공연 회차 ID")
                                    .attributes(
                                        Attributes.key("scheduleId").value(requestBody.scheduleId)
                                    ),
                                fieldWithPath("seatNo").type(JsonFieldType.NUMBER).description("예약할 좌석 번호").attributes(
                                    Attributes.key("seatNo").value(requestBody.seatNo)
                                ),
                            )
                            .build()
                    )
                )
            )
    }
}
