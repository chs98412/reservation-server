package kr.hhplus.be.server.eventListener.concert

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kr.hhplus.be.server.application.dataPlatform.DataPlatformService
import kr.hhplus.be.server.common.exception.NotFoundReservationException
import kr.hhplus.be.server.domain.KafkaTopic
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import kr.hhplus.be.server.eventListener.concert.model.ReservationEvent
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ConcertEventListener(
    private val reservationRepository: ReservationRepository,
    private val queueCacheRepository: QueueCacheRepository,
    private val dataPlatformService: DataPlatformService,
) {
    private val objectMapper = jacksonObjectMapper()

    @KafkaListener(
        topics = [KafkaTopic.RESERVE_COMPLETE],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun handleReserveEvent(event: String) {
        val reservationEvent = objectMapper.readValue(event, ReservationEvent::class.java)
        reservationRepository.findByIdOrNull(reservationEvent.reservationId)?.let {
            queueCacheRepository.removeFromActive(it.concertId, reservationEvent.accountId)
            dataPlatformService.sendData(it)
        } ?: throw NotFoundReservationException()
    }
}