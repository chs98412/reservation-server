package kr.hhplus.be.server.controller

import kr.hhplus.be.server.controller.model.request.BalanceChargeRequest
import kr.hhplus.be.server.controller.model.request.PaymentRequest
import kr.hhplus.be.server.controller.model.response.BalanceDetailResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PointController {

    @PostMapping("/charge")
    fun chargeBalance(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestBody request: BalanceChargeRequest
    ): ResponseEntity<Void> {
        return ResponseEntity.noContent().build()
    }

    @GetMapping
    fun getBalance(
        @RequestHeader("X-ACCOUNT-ID") accountId: String
    ): ResponseEntity<BalanceDetailResponse> {
        return ResponseEntity.ok(BalanceDetailResponse.mockResponse)
    }

    @PostMapping("/payment")
    fun processPayment(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestBody request: PaymentRequest
    ): ResponseEntity<Void> {
        return ResponseEntity.noContent().build()
    }
}