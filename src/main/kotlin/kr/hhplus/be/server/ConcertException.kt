package kr.hhplus.be.server

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

abstract class ConcertException(
    override val message: String,
) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundConcertException(
    override val message: String = "존재하지 않는 콘서트입니다.",
) : ConcertException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class AlreadyReservedSeatException(
    override val message: String = "이미 예약된 좌석입니다."
) : ConcertException(message)