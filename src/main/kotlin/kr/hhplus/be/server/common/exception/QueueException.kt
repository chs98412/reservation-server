package kr.hhplus.be.server.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

abstract class QueueException(
    override val message: String,
) : RuntimeException(message)

@ResponseStatus(HttpStatus.CONFLICT)
class AlreadyAssignedQueueAccountException(
    override val message: String = "이미 대기열에 참가한 사용자입니다.",
) : QueueException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidQueueTokenException(
    override val message: String = "유효하지 않은 대기열 토큰입니다.",
) : QueueException(message)


@ResponseStatus(HttpStatus.NOT_FOUND)
class AccountNotFoundInQueueException(
    override val message: String = "대기열에 존재하지 않습니다.",
) : QueueException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class QueueNotFoundException(
    override val message: String = "대기열이 존재하지 않습니다.",
) : QueueException(message)
