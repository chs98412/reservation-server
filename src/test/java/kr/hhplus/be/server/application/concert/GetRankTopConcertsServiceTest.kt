package kr.hhplus.be.server.application.concert

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertRankCacheRepository
import kr.hhplus.be.server.domain.concert.ConcertRepository
import java.time.LocalDate

class GetRankTopConcertsServiceTest : StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val concertRepository = mockk<ConcertRepository>()
    val concertRankCacheRepository = mockk<ConcertRankCacheRepository>()
    val service = GetRankTopConcertsService(concertRepository, concertRankCacheRepository)

    "캐시에 값이 있으면 DB 조회 없이 반환" {
        every { concertRankCacheRepository.getTop(any()) } returns listOf(1)
        every { concertRepository.findAllById(listOf(1L)) } returns listOf(
            Concert(id = 1L, name = "Test", startDate = LocalDate.now(), endDate = LocalDate.now()),
        )

        val result = service.execute(30)
        result shouldHaveSize 1
        verify(exactly = 0) { concertRepository.findTopSellConcerts(any(), any()) }
    }
})
