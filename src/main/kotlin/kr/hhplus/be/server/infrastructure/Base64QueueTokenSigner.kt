package kr.hhplus.be.server.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kr.hhplus.be.server.common.exception.InvalidQueueTokenException
import kr.hhplus.be.server.application.queue.QueueTokenSigner
import kr.hhplus.be.server.domain.queue.QueueToken
import org.springframework.stereotype.Component
import java.util.*

@Component
class Base64QueueTokenSigner : QueueTokenSigner {
    private val objectMapper = jacksonObjectMapper()

    override fun encode(token: QueueToken): String {
        return Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(token).toByteArray())
    }

    override fun decode(tokenString: String): QueueToken {
        return runCatching {
            jacksonObjectMapper().readValue(
                String(Base64.getDecoder().decode(tokenString)),
                QueueToken::class.java
            )
        }.getOrElse {
            throw InvalidQueueTokenException()
        }

    }
}