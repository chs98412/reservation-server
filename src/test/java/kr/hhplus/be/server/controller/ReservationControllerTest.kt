package kr.hhplus.be.server.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.headerWithName
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kr.hhplus.be.server.controller.model.request.PaymentRequest
import kr.hhplus.be.server.controller.model.request.SeatReservationRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate


@ExtendWith(RestDocumentationExtension::class)
@WebMvcTest
class ReservationControllerTest {
    @Autowired
    lateinit var context: WebApplicationContext

    lateinit var mockMvc: MockMvc

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
        mockMvc.perform(
            get("/reservation/available-dates?concert-id={concert-id}", 1)
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
                                fieldWithPath("availableDates").type(JsonFieldType.ARRAY).description("예약 가능 날짜 목록"),
                            )
                            .build()
                    )
                )
            )
    }


    @Test
    fun `예약 가능 좌석 조회 API`() {
        mockMvc.perform(
            get("/reservation/available-seats?date={date}", LocalDate.now())
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
                                parameterWithName("date").description("검색 일자")
                            )
                            .responseFields(
                                fieldWithPath("availableSeats").type(JsonFieldType.ARRAY).description("예약 가능 좌석 목록"),
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    fun `좌석 예약 요청 API`() {
        val requestBody = SeatReservationRequest(seatId = 1)

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
                                fieldWithPath("seatId").type(JsonFieldType.NUMBER).description("예약할 좌석 번호")
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    fun `결제 처리 API`() {
        val request = PaymentRequest(
            reservationId = 1,
        )

        val json = jacksonObjectMapper().writeValueAsString(request)

        mockMvc.perform(
            post("/point/payment")
                .header("X-ACCOUNT-ID", "account123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isNoContent)
            .andDo(
                document(
                    "process-payment",
                    preprocessResponse(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("결제 처리 API")
                            .requestHeaders(
                                headerWithName("X-ACCOUNT-ID").description("사용자 ID"),
                            )
                            .requestFields(
                                fieldWithPath("reservationId").type(JsonFieldType.NUMBER).description("예약 번호"),
                            )
                            .build()
                    )
                )
            )
    }

}
