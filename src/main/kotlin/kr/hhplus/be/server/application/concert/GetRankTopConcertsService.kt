package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertRankCacheRepository
import kr.hhplus.be.server.domain.concert.ConcertRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

data class TopRankedConcertResponse(
    val concertId: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    companion object {
        fun from(concert: Concert) = TopRankedConcertResponse(
            concertId = concert.id,
            name = concert.name,
            startDate = concert.startDate,
            endDate = concert.endDate,
        )
    }
}

@Service
class GetRankTopConcertsService(
    private val concertRepository: ConcertRepository,
    private val concertRankCacheRepository: ConcertRankCacheRepository,
) {
    fun execute(limit: Int = 30): List<TopRankedConcertResponse> {
        val ids = concertRankCacheRepository.getTop(limit).ifEmpty {
            val rows = concertRepository.findTopSellConcerts()
            if (rows.isEmpty()) return emptyList()

            concertRankCacheRepository.clear()
            rows.forEach {
                concertRankCacheRepository.add(it.concertId, it.score())
            }
            concertRankCacheRepository.getTop(limit)
        }
        val map = concertRepository.findAllById(ids).associateBy { it.id }
        return ids.mapNotNull(map::get).map(TopRankedConcertResponse::from)
    }
}