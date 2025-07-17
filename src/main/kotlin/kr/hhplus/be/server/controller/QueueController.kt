package kr.hhplus.be.server.controller

import kr.hhplus.be.server.controller.model.response.QueueStatusDetailResponse
import kr.hhplus.be.server.controller.model.response.QueueTokenCreateResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/queue")
class QueueController {
    @PostMapping("/token")
    fun createToken(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
    ): ResponseEntity<QueueTokenCreateResponse> {
        return ResponseEntity.ok(QueueTokenCreateResponse.mockResponse)
    }

    @GetMapping("/status")
    fun getStatus(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
    ): ResponseEntity<QueueStatusDetailResponse> {
        return ResponseEntity.ok(QueueStatusDetailResponse.mockResponse)
    }
}