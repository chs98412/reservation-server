package kr.hhplus.be.server.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.headerWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import kr.hhplus.be.server.application.point.PayService
import kr.hhplus.be.server.application.point.model.BalanceFetchSummary
import kr.hhplus.be.server.controller.model.request.BalanceChargeRequest
import kr.hhplus.be.server.controller.model.request.PaymentRequest
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

@TestConfiguration
class PointMockConfig {
    @Bean
    fun payService(): PayService = mockk(relaxed = true)
}

@ExtendWith(RestDocumentationExtension::class)
@WebMvcTest(controllers = [PayController::class])
@Import(PointMockConfig::class)
class PayControllerTest {
    @Autowired
    lateinit var context: WebApplicationContext

    lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @Autowired
    lateinit var payService: PayService

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
            )
            .build()
    }

    @Test
    fun `잔액 충전 API`() {
        val request = BalanceChargeRequest(amount = 10000)
        val json = objectMapper.writeValueAsString(request)

        justRun { payService.charge(any(), any()) }
        mockMvc.perform(
            post("/point/charge")
                .header("X-ACCOUNT-ID", "account123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isNoContent)
            .andDo(
                document(
                    "charge-balance",
                    preprocessResponse(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("잔액 충전 API")
                            .requestHeaders(
                                headerWithName("X-ACCOUNT-ID").description("사용자 식별 ID")
                            )
                            .requestFields(
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("충전 금액")
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    fun `잔액 조회 API`() {
        val summary = BalanceFetchSummary(100L)
        every { payService.getBalance(any()) } returns summary

        mockMvc.perform(
            get("/point")
                .header("X-ACCOUNT-ID", "account123")
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-balance",
                    preprocessResponse(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("잔액 조회 API")
                            .requestHeaders(
                                headerWithName("X-ACCOUNT-ID").description("사용자 식별 ID")
                            )
                            .responseFields(
                                fieldWithPath("point").type(JsonFieldType.NUMBER).description("현재 잔액")
                                    .attributes(
                                        Attributes.key("point").value(summary.point)
                                    ),
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

        justRun { payService.processPayment(any()) }
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
