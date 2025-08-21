package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertRepository
import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.time.LocalDate

data class TopRankedConcertResponse(
    val concertId: Long,

    val name: String,

    val startDate: LocalDate,

    val endDate: LocalDate,

    val views: Long,

    val likes: Long,
) {
    companion object {
        fun from(concert: Concert) = TopRankedConcertResponse(
            concertId = concert.id,
            name = concert.name,
            startDate = concert.startDate,
            endDate = concert.endDate,
            views = concert.views,
            likes = concert.likes,
        )
    }
}

@Service
class GetRankTopConcertsService(
    private val concertRepository: ConcertRepository,
    private val redissonClient: RedissonClient,
) {
    fun execute(limit: Int = 30): List<TopRankedConcertResponse> {
        val z: RScoredSortedSet<String> = redissonClient.getScoredSortedSet("top-sell")
        var entries = z.entryRangeReversed(0, limit - 1)

        if (entries.isEmpty()) {
            rebuildGenreCache(z)
            entries = z.entryRangeReversed(0, limit - 1)
            if (entries.isEmpty()) return emptyList()
        }

        val ids = entries.map { it.value.toLong() }
        val map = concertRepository.findAllById(ids).associateBy { it.id }
        return ids.mapNotNull(map::get).map(TopRankedConcertResponse::from)
    }

    private fun rebuildGenreCache(z: RScoredSortedSet<String>) {
        val rows = concertRepository.findTopSellConcerts()
        if (rows.isEmpty()) return

        z.clear()
        rows.forEach { r ->
            val score = r.score()
            if (score > 0.0) z.add(score, r.concertId.toString())
        }
    }
}