package kr.hhplus.be.server.application.concert

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.Genre
import org.redisson.api.RedissonClient
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Duration
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
    private val objectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    fun execute(genre: Genre): List<TopRankedConcertResponse> {
        val bucket = redissonClient.getBucket<String>("topRankedConcerts:${genre.name}")
        bucket.get()?.let {
            return objectMapper.readValue<List<TopRankedConcertResponse>>(it)
        } ?: let {
            return concertRepository.findTopByGenre(genre, PageRequest.of(0, 30))
                .map(TopRankedConcertResponse::from)
                .also {
                    bucket.set(objectMapper.writeValueAsString(it), Duration.ofMinutes(5))
                }
        }
    }
}