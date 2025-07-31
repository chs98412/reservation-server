package kr.hhplus.be.server.application.concert

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.common.exception.NotFoundConcertException
import kr.hhplus.be.server.domain.concert.ConcertRepository

class GetAvailableDatesServiceTest : BehaviorSpec({
    val concertRepository = mockk<ConcertRepository>()
    val getAvailableDatesService = GetAvailableDatesService(concertRepository)
    Given("예약 가능 날짜 조회에서") {
        val concertId = "nonexistent-concert"

        When("해당 concertId에 대한 콘서트가 존재하지 않으면") {
            every { concertRepository.findByConcertId(concertId) } returns null

            Then("NotFoundConcertException이 발생해야 한다") {
                shouldThrow<NotFoundConcertException> {
                    getAvailableDatesService.execute(concertId)
                }
            }
        }
    }
})
