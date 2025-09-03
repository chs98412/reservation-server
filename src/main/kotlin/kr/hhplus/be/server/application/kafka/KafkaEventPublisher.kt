package kr.hhplus.be.server.application.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component


@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    fun publish(topic: String, event: Any) {
        kafkaTemplate.send(
            topic,
            event,
        )
    }
}
