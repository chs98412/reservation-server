package kr.hhplus.be.server.presentation

import kr.hhplus.be.server.application.queue.CreateTokenUseCase
import kr.hhplus.be.server.application.queue.GetStatusUseCase
import kr.hhplus.be.server.application.queue.QueueStatusResponse
import kr.hhplus.be.server.application.queue.QueueTokenResponse
import kr.hhplus.be.server.infrastructure.withLock
import org.redisson.api.RedissonClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/queue")
class QueueController(
    private val createTokenUseCase: CreateTokenUseCase,
    private val getStatusUseCase: GetStatusUseCase,
    private val redisson: RedissonClient,
) {
    @PostMapping("/token/{concert-id}")
    fun createToken(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @PathVariable("concert-id") concertId: Long,
    ): ResponseEntity<QueueTokenResponse> {
        val response = redisson.withLock(key = "create-token:${concertId}:${accountId}") {
            createTokenUseCase.execute(accountId, concertId)
        }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/status")
    fun getStatus(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestHeader("X-QUEUE-TOKEN-ID") queueTokenId: String,
    ): ResponseEntity<QueueStatusResponse> {
        return ResponseEntity.ok(getStatusUseCase.execute(queueTokenId))
    }
}