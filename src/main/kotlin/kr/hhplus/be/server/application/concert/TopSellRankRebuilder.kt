package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.ConcertRankCacheRepository
import kr.hhplus.be.server.domain.concert.ConcertRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TopSellRankRebuilder(
    private val concertRepository: ConcertRepository,
    private val concertRankCacheRepository: ConcertRankCacheRepository,
) {
    @Scheduled(fixedDelay = 50_000)
    fun rebuildTopSellConcerts() {
        val rows = concertRepository.findTopSellConcerts()
        if (rows.isEmpty()) return

        concertRankCacheRepository.clear()
        rows.forEach {
            concertRankCacheRepository.add(it.concertId, it.score())
        }
    }
}