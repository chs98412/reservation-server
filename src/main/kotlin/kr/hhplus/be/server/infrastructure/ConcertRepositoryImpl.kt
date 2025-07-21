package kr.hhplus.be.server.infrastructure

import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.ConcertSchedule
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ConcertRepositoryImpl : ConcertRepository {
    private val concertStorage = ConcurrentHashMap<String, Concert>()
    private val scheduleStorage = ConcurrentHashMap<String, MutableList<ConcertSchedule>>()

    override fun findByConcertId(concertId: String): Concert? {
        return concertStorage[concertId]
    }

    override fun findAllByConcertId(concertId: String): List<ConcertSchedule> {
        return scheduleStorage[concertId]?.toList() ?: emptyList()
    }
}