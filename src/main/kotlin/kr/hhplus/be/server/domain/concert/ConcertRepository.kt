package kr.hhplus.be.server.domain.concert

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ConcertRepository : JpaRepository<Concert, Long> {
    @Query(
        """
        select c.id as concertId,
               c.startDate as startDate,
               count(r) as capacity,
               sum(case when r.status = :paid then 1 else 0 end) as sold
        from Concert c
        join Reservation r on r.concertId = c.id
        where r.date >= :since
        group by c.id, c.startDate
        """
    )
    fun findTopSellConcerts(
        @Param("paid") paid: Status = Status.PAID,
        @Param("since") since: LocalDate = LocalDate.now()
    ): List<TopSellConcert>
}