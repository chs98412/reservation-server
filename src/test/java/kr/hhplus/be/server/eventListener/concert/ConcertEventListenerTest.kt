package kr.hhplus.be.server.eventListener.concert

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.application.dataPlatform.DataPlatformService
import kr.hhplus.be.server.common.exception.NotFoundReservationException
import kr.hhplus.be.server.domain.concert.Reservation
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.queue.QueueCacheRepository
import kr.hhplus.be.server.eventListener.concert.model.ReservationEvent
import org.springframework.data.repository.findByIdOrNull

class ConcertEventListenerTest : BehaviorSpec({
    val objectMapper = jacksonObjectMapper()

    val reservationRepository = mockk<ReservationRepository>()
    val dataPlatformService = mockk<DataPlatformService>(relaxed = true)
    val queueCacheRepository = mockk<QueueCacheRepository>(relaxed = true)

    val concertEventListener = ConcertEventListener(
        reservationRepository = reservationRepository,
        dataPlatformService = dataPlatformService,
        queueCacheRepository = queueCacheRepository,
    )

    Given("예약이 존재하는 경우") {
        val reservation = mockk<Reservation>(relaxed = true)

        every { reservationRepository.findByIdOrNull(100L) } returns reservation
        val event = objectMapper.writeValueAsString(ReservationEvent("accountId", 100L))
        When("ReservationEvent가 발생하면") {
            concertEventListener.handleReserveEvent(event)

            Then("removeFromActive와 sendData가 호출된다") {
                verify(exactly = 1) { queueCacheRepository.removeFromActive(any(), any()) }
                verify(exactly = 1) { dataPlatformService.sendData(reservation) }
            }
        }
    }

    Given("예약이 존재하지 않는 경우") {
        val event = objectMapper.writeValueAsString(ReservationEvent("accountId", 200L))
        every { reservationRepository.findByIdOrNull(200L) } returns null

        Then("NotFoundReservationException이 발생해야 한다") {
            shouldThrow<NotFoundReservationException> {
                concertEventListener.handleReserveEvent(event)
            }
        }
    }
})
