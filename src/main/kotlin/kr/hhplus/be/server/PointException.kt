package kr.hhplus.be.server

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

abstract class PointException(
    override val message: String,
) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundBalanceException(
    override val message: String = "계좌가 존재하지 않습니다.",
) : PointException(message)