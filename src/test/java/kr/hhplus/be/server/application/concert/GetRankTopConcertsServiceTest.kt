package kr.hhplus.be.server.application.concert

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertRepository
import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.redisson.client.protocol.ScoredEntry
import java.time.LocalDate

class GetRankTopConcertsServiceTest : StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val concertRepository = mockk<ConcertRepository>()
    val redissonClient = mockk<RedissonClient>()
    val scoredSet = mockk<RScoredSortedSet<String>>(relaxed = true)
    val service = GetRankTopConcertsService(concertRepository, redissonClient)

    "캐시에 값이 있으면 DB 조회 없이 반환" {
        every { redissonClient.getScoredSortedSet<String>("top-sell") } returns scoredSet
        every { scoredSet.entryRangeReversed(0, 29) } returns listOf(
            ScoredEntry(1.0, "1")
        )
        every { concertRepository.findAllById(listOf(1L)) } returns listOf(
            Concert(id = 1L, name = "Test", startDate = LocalDate.now(), endDate = LocalDate.now()),
        )

        val result = service.execute(30)
        result shouldHaveSize 1
        verify(exactly = 0) { concertRepository.findTopSellConcerts(any(), any()) }
    }
})
