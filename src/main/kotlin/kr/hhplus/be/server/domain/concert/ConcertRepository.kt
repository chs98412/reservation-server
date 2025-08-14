package kr.hhplus.be.server.domain.concert

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ConcertRepository : JpaRepository<Concert, Long> {
    @Query(
        """
    select c
    from Concert c
    where c.genre = :genre
    order by (c.views+(c.likes*30))  
        * EXP(-ABS(DATEDIFF(c.startDate, CURDATE())) / 7) DESC,c.id
    """
    )
    fun findTopByGenre(
        @Param("genre") genre: Genre,
        pageable: Pageable
    ): List<Concert>
}