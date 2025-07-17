package kr.hhplus.be.server.controller

import kr.hhplus.be.server.controller.model.response.QueueTokenCreateResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/queue")
class QueueController {
    @PostMapping("/token")
    fun createToken(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
    ): ResponseEntity<QueueTokenCreateResponse> {
        return ResponseEntity.ok(QueueTokenCreateResponse.mockResponse)
    }
}