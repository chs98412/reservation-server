package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.ConcertRepository
import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TopSellRankRebuilder(
    private val concertRepository: ConcertRepository,
    private val redisson: RedissonClient,
) {
    @Scheduled(fixedDelay = 60_000)
    fun rebuildAllGenres() {
        rebuildTopSellConcerts()
    }

    fun rebuildTopSellConcerts() {
        val rows = concertRepository.findTopSellConcerts()
        val z: RScoredSortedSet<String> = redisson.getScoredSortedSet("top-sell")
        z.clear()

        rows.forEach { r ->
            val s = r.score()
            if (s > 0.0) z.add(s, r.concertId.toString())
        }
    }
}
