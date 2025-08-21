package kr.hhplus.be.server.domain.concert

interface ConcertRankCacheRepository {
    fun getTop(limit: Int): List<Long>
    fun clear()
    fun add(concertId: Long, score: Double)
}

