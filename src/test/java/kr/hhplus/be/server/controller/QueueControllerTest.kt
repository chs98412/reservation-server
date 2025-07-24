package kr.hhplus.be.server.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.headerWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.application.QueueService
import kr.hhplus.be.server.application.model.QueueTokenSummary
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
    fun queueService(): QueueService = mockk(relaxed = true)
}

@ExtendWith(RestDocumentationExtension::class)
@Import(QueueMockConfig::class)
@WebMvcTest
class QueueControllerTest {
    @Autowired
    lateinit var context: WebApplicationContext

    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var queueService: QueueService


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
        val summary = QueueTokenSummary(token = "token")
        every { queueService.createToken(any()) } returns QueueTokenSummary(token = "token")

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
    fun `대기 번호 조회 API`() {
        mockMvc.perform(
            get("/queue/status")
                .header("X-ACCOUNT-ID", "account123")
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
                                headerWithName("X-ACCOUNT-ID").description("사용자 식별 헤더")
                            )
                            .responseFields(
                                fieldWithPath("queueNumber").type(JsonFieldType.NUMBER).description("대기 번호"),
                                fieldWithPath("estimatedWaitSeconds").type(JsonFieldType.NUMBER)
                                    .description("예상 대기 시간"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("대기 상태"),
                            )
                            .build()
                    )
                )
            )
    }
}
