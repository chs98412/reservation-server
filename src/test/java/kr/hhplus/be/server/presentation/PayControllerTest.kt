package kr.hhplus.be.server.presentation

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.headerWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import kr.hhplus.be.server.application.point.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.snippet.Attributes
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.concurrent.TimeUnit

@ExtendWith(RestDocumentationExtension::class)
@ContextConfiguration(classes = [PayController::class])
@WebMvcTest(controllers = [PayController::class])
class PayControllerTest {
    @Autowired
    lateinit var context: WebApplicationContext

    lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @MockkBean(relaxed = true)
    lateinit var chargePointUseCase: ChargePointUseCase

    @MockkBean(relaxed = true)
    lateinit var getBalanceUseCase: GetBalanceUseCase

    @MockkBean(relaxed = true)
    lateinit var processPaymentUseCase: ProcessPaymentUseCase

    @MockkBean(relaxed = true)
    lateinit var redissonClient: RedissonClient

    val rlock = mockk<RLock>(relaxed = true)

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
            )
            .build()
        every { redissonClient.getLock(any()) } returns rlock
        every { redissonClient.getFairLock(any()) } returns rlock
        every { rlock.tryLock(any<Long>(), any<Long>(), any<TimeUnit>()) } returns true
        every { rlock.isHeldByCurrentThread } returns false
        justRun { rlock.unlock() }
    }

    @Test
    fun `잔액 충전 API`() {
        val request = BalanceChargeRequest(amount = 10000)
        val json = objectMapper.writeValueAsString(request)

        justRun { chargePointUseCase.execute(any(), any()) }
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
        val summary = BalanceFetchResponse(100L)
        every { getBalanceUseCase.execute(any()) } returns summary

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
        justRun { processPaymentUseCase.execute(any()) }

        mockMvc.perform(
            post("/point/payment")
                .header("X-ACCOUNT-ID", "account123")
                .contentType(MediaType.APPLICATION_JSON)
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
                            .build()
                    )
                )
            )
    }

}
