package kr.hhplus.be.server.domain.concert

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.max

interface TopSellConcert {
    val concertId: Long
    val startDate: LocalDate
    val capacity: Long
    val sold: Long

    fun score(): Double {
        if (capacity <= 0) return 0.0
        val openedAt = startDate.atStartOfDay()
        val hours = max(1.0, ChronoUnit.MINUTES.between(openedAt, LocalDateTime.now()).toDouble() / 60.0)
        if (hours <= 0.0) return 0.0
        return sold.toDouble() / (hours * capacity.toDouble())
    }
}