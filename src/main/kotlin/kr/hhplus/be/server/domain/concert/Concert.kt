package kr.hhplus.be.server.domain.concert

import java.time.LocalDate

class Concert(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)