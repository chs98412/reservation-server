package kr.hhplus.be.server.infrastructure

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kr.hhplus.be.server.application.queue.QueueTokenSigner
import kr.hhplus.be.server.common.exception.InvalidQueueTokenException
import kr.hhplus.be.server.domain.queue.QueueToken
import org.springframework.stereotype.Component
import java.util.*

@Component
class Base64QueueTokenSigner : QueueTokenSigner {
    private val objectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    override fun encode(token: QueueToken): String {
        return Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(token).toByteArray())
    }

    override fun decode(tokenString: String): QueueToken {
        return runCatching {
            objectMapper.readValue(
                String(Base64.getDecoder().decode(tokenString)),
                QueueToken::class.java
            )
        }.getOrElse {
            throw InvalidQueueTokenException()
        }

    }
}