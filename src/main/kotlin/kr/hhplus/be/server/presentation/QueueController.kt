package kr.hhplus.be.server.presentation

import kr.hhplus.be.server.application.queue.CreateTokenUseCase
import kr.hhplus.be.server.application.queue.GetStatusUseCase
import kr.hhplus.be.server.application.queue.QueueStatusResponse
import kr.hhplus.be.server.application.queue.QueueTokenResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/queue")
class QueueController(
    private val createTokenUseCase: CreateTokenUseCase,
    private val getStatusUseCase: GetStatusUseCase,
) {
    @PostMapping("/token")
    fun createToken(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
    ): ResponseEntity<QueueTokenResponse> {
        return ResponseEntity.ok(createTokenUseCase.execute(accountId))
    }

    @GetMapping("/status")
    fun getStatus(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestHeader("X-QUEUE-TOKEN-ID") queueTokenId: String,
    ): ResponseEntity<QueueStatusResponse> {
        return ResponseEntity.ok(getStatusUseCase.execute(queueTokenId))
    }
}