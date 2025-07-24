package kr.hhplus.be.server

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

abstract class QueueException(
    override val message: String,
) : RuntimeException(message)

@ResponseStatus(HttpStatus.CONFLICT)
class AlreadyAssignedQueueAccountException(
    override val message: String = "이미 대기열에 참가한 사용자입니다.",
) : QueueException(message)