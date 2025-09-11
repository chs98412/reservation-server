package kr.hhplus.be.server.config.kafka

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory

@Configuration
class KafkaConsumerConfig(
    private val consumerFactory: ConsumerFactory<String, Any>
) {
    @Bean
    fun batchKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = consumerFactory
        factory.isBatchListener = true
        factory.setConcurrency(1)
        factory.containerProperties.pollTimeout = 3000
        factory.containerProperties.idleBetweenPolls = 10_000L //10초 간격
        return factory
    }
}
