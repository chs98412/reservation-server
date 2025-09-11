package kr.hhplus.be.server.eventListener.concert

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kr.hhplus.be.server.domain.KafkaTopic
import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import kr.hhplus.be.server.domain.queue.QueueToken
import kr.hhplus.be.server.domain.queue.QueueToken.Companion.EXPIRE_THRESHOLD
import kr.hhplus.be.server.eventListener.concert.model.QueueJoinEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime


@Component
class QueueEventListener(
    private val queueCacheRepository: QueueCacheRepository
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val objectMapper = jacksonObjectMapper()

    @KafkaListener(
        topics = [KafkaTopic.QUEUE_JOIN],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "batchKafkaListenerContainerFactory"
    )
    fun increaseEntranceNumber(messages: List<String>) {
        val events: List<QueueJoinEvent> = messages.map { msg ->
            objectMapper.readValue(msg, QueueJoinEvent::class.java)
        }
        println("time: ${LocalDateTime.now()} events = ${events}")
        events.take(QueueToken.QUEUE_ENTRANCE_LIMIT.toInt()).forEach { event ->
            queueCacheRepository.addActive(
                event.concertId,
                event.accountId,
                EXPIRE_THRESHOLD
            )
        }
    }
}