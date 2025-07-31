package kr.hhplus.be.server.presentation

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.headerWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.application.queue.CreateTokenUseCase
import kr.hhplus.be.server.application.queue.GetStatusUseCase
import kr.hhplus.be.server.application.queue.QueueStatusResponse
import kr.hhplus.be.server.application.queue.QueueTokenResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
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
class QueueMockConfig {
    @Bean
    fun createTokenUseCase(): CreateTokenUseCase = mockk(relaxed = true)

    @Bean
    fun getStatusUseCase(): GetStatusUseCase = mockk(relaxed = true)
}

@ExtendWith(RestDocumentationExtension::class)
@Import(QueueMockConfig::class)
@WebMvcTest(controllers = [QueueController::class])
class QueueControllerTest {
    @Autowired
    lateinit var context: WebApplicationContext

    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var createTokenUseCase: CreateTokenUseCase

    @Autowired
    lateinit var getStatusUseCase: GetStatusUseCase


    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        MockKAnnotations.init(this)

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
            )
            .build()
    }

    @Test
    fun `대기열 토큰 발급 API`() {
        val summary = QueueTokenResponse(token = "token")
        every { createTokenUseCase.execute(any()) } returns QueueTokenResponse(token = "token")

        mockMvc.perform(
            post("/queue/token")
                .header("X-ACCOUNT-ID", "account123")
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "create-queue-token",
                    preprocessResponse(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("대기열 토큰 발급")
                            .requestHeaders(
                                headerWithName("X-ACCOUNT-ID").description("사용자 식별 헤더")
                            )
                            .responseFields(
                                fieldWithPath("token").type(JsonFieldType.STRING).description("토큰 정보")
                                    .attributes(Attributes.key("token").value(summary.token)),
                            )
                            .build()
                    )
                )
            )
    }


    @Test
    fun `대기 상태 조회 API`() {
        val summary = QueueStatusResponse(queueNumber = 10, isAllowedToEnter = true, estimateWaitTime = 1000)
        every { getStatusUseCase.execute(any()) } returns summary

        mockMvc.perform(
            get("/queue/status")
                .header("X-ACCOUNT-ID", "account123")
                .header("X-QUEUE-TOKEN-ID", "queue-token")
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-queue-status",
                    preprocessResponse(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("대기열 대기번호 조회")
                            .requestHeaders(
                                headerWithName("X-ACCOUNT-ID").description("사용자 식별 헤더"),
                                headerWithName("X-QUEUE-TOKEN-ID").description("대기열 토큰 헤더")
                            )
                            .responseFields(
                                fieldWithPath("queueNumber").type(JsonFieldType.NUMBER).description("대기 번호")
                                    .attributes(Attributes.key("queueNumber").value(summary.queueNumber)),
                                fieldWithPath("isAllowedToEnter").type(JsonFieldType.BOOLEAN).description("입장 가능 여부")
                                    .attributes(Attributes.key("isAllowedToEnter").value(summary.isAllowedToEnter)),
                                fieldWithPath("estimateWaitTime").type(JsonFieldType.NUMBER).description("예상 대기시간")
                                    .attributes(Attributes.key("estimateWaitTime").value(summary.estimateWaitTime)),
                            )
                            .build()
                    )
                )
            )
    }
}
