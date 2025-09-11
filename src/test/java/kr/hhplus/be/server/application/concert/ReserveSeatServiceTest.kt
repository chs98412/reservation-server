package kr.hhplus.be.server.application.concert

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kr.hhplus.be.server.application.kafka.KafkaEventPublisher
import kr.hhplus.be.server.common.exception.AlreadyReservedSeatException
import kr.hhplus.be.server.common.exception.NotFoundConcertException
import kr.hhplus.be.server.domain.KafkaTopic
import kr.hhplus.be.server.domain.concert.Reservation
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.eventListener.concert.model.ReservationEvent

class ReserveSeatServiceTest : BehaviorSpec({
    val reservationRepository = mockk<ReservationRepository>()
    val kafkaEventPublisher = mockk<KafkaEventPublisher>(relaxed = true)
    val reserveSeatService = ReserveSeatService(reservationRepository, kafkaEventPublisher)

    val command = SeatReservationCommand(
        concertId = 1L,
        seatNo = 1,
        accountId = "user-123"
    )

    Given("예약 대상 좌석이 존재하지 않을 때") {
        every {
            reservationRepository.findByConcertIdAndSeatNo(
                command.concertId, command.seatNo
            )
        } returns null

        Then("NotFoundConcertException이 발생해야 한다") {
            shouldThrow<NotFoundConcertException> {
                reserveSeatService.execute(command)
            }
        }
    }

    Given("좌석이 이미 예약되어 있을 때") {
        val reservedSeat = mockk<Reservation>(relaxed = true)
        every { reservedSeat.isUnAvailableToReserve() } returns true

        every {
            reservationRepository.findByConcertIdAndSeatNo(
                command.concertId, command.seatNo
            )
        } returns reservedSeat

        Then("AlreadyReservedSeatException이 발생해야 한다") {
            shouldThrow<AlreadyReservedSeatException> {
                reserveSeatService.execute(command)
            }
        }
    }

    Given("좌석이 예약 가능한 상태일 때") {
        val seat = mockk<Reservation>(relaxed = true)
        every { seat.isUnAvailableToReserve() } returns false
        every { seat.id } returns 1

        every {
            reservationRepository.findByConcertIdAndSeatNo(
                command.concertId, command.seatNo
            )
        } returns seat

        When("reserveSeat를 호출하면") {
            reserveSeatService.execute(command)

            Then("seat.reserve(accountId)가 호출된다") {
                verify(exactly = 1) { seat.reserve(command.accountId) }
            }
            Then("ReservationEvent가 발행된다") {
                val slot = slot<ReservationEvent>()
                verify { kafkaEventPublisher.publish(KafkaTopic.RESERVE_COMPLETE, capture(slot)) }
                slot.captured.reservationId shouldBe 1
            }
        }
    }
})
