package kr.hhplus.be.server.application.concert

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.AlreadyReservedSeatException
import kr.hhplus.be.server.NotFoundConcertException
import kr.hhplus.be.server.application.concert.model.SeatReservationCommand
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.Reservation

class ConcertServiceTest : BehaviorSpec({
    val concertRepository = mockk<ConcertRepository>()
    val concertService = ConcertService(concertRepository)
    Given("예약 가능 날짜 조회에서") {
        val concertId = "nonexistent-concert"

        When("해당 concertId에 대한 콘서트가 존재하지 않으면") {
            every { concertRepository.findByConcertId(concertId) } returns null

            Then("NotFoundConcertException이 발생해야 한다") {
                shouldThrow<NotFoundConcertException> {
                    concertService.getAvailableDates(concertId)
                }
            }
        }
    }
    val command = SeatReservationCommand(
        concertId = "concert-1",
        scheduleId = 1,
        seatNo = 1,
        accountId = "user-123"
    )

    Given("예약 대상 좌석이 존재하지 않을 때") {
        every {
            concertRepository.findByConcertIdAndScheduleIdAndSeatNo(
                command.concertId, command.scheduleId, command.seatNo
            )
        } returns null

        Then("NotFoundConcertException이 발생해야 한다") {
            shouldThrow<NotFoundConcertException> {
                concertService.reserveSeat(command)
            }
        }
    }

    Given("좌석이 이미 예약되어 있을 때") {
        val reservedSeat = mockk<Reservation>(relaxed = true)
        every { reservedSeat.isUnAvailableToReserve() } returns true

        every {
            concertRepository.findByConcertIdAndScheduleIdAndSeatNo(
                command.concertId, command.scheduleId, command.seatNo
            )
        } returns reservedSeat

        Then("AlreadyReservedSeatException이 발생해야 한다") {
            shouldThrow<AlreadyReservedSeatException> {
                concertService.reserveSeat(command)
            }
        }
    }

    Given("좌석이 예약 가능한 상태일 때") {
        val seat = mockk<Reservation>(relaxed = true)
        every { seat.isUnAvailableToReserve() } returns false

        every {
            concertRepository.findByConcertIdAndScheduleIdAndSeatNo(
                command.concertId, command.scheduleId, command.seatNo
            )
        } returns seat

        When("reserveSeat를 호출하면") {
            concertService.reserveSeat(command)

            Then("seat.reserve(accountId)가 호출된다") {
                verify(exactly = 1) { seat.reserve(command.accountId) }
            }
        }
    }
})
