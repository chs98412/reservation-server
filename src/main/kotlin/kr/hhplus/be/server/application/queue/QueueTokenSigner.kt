package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.domain.queue.QueueToken

interface QueueTokenSigner {
    fun encode(token: QueueToken): String
    fun decode(tokenString: String): QueueToken
}