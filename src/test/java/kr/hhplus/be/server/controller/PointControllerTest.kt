package kr.hhplus.be.server.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext


@ExtendWith(RestDocumentationExtension::class)
@WebMvcTest
class PointControllerTest {
    @Autowired
    lateinit var context: WebApplicationContext

    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
            )
            .build()
    }

    @Test
    fun `포인트 조회 api (샘플)`() {
        mockMvc.perform(get("/point/{id}", 1))
            .andExpect(status().isOk)
            .andDo(
                document(
                    "get-point",
                    preprocessResponse(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("사용자 포인트 조회")
                            .pathParameters(
                                parameterWithName("id").description("사용자 ID")
                            )
                            .responseFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("point").description("보유 포인트")
                            )
                            .build()
                    )
                )
            )
    }
}
