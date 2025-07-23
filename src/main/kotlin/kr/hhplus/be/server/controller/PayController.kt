package kr.hhplus.be.server.controller

import kr.hhplus.be.server.application.point.PayService
import kr.hhplus.be.server.controller.model.request.BalanceChargeRequest
import kr.hhplus.be.server.controller.model.response.BalanceDetailResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PayController(
    private val payService: PayService,
) {

    @PostMapping("/charge")
    fun chargeBalance(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestBody request: BalanceChargeRequest
    ): ResponseEntity<Void> {
        payService.charge(accountId, request.amount)
        return ResponseEntity.noContent().build()
    }

    @GetMapping
    fun getBalance(
        @RequestHeader("X-ACCOUNT-ID") accountId: String
    ): ResponseEntity<BalanceDetailResponse> {
        payService.getBalance(accountId)

        return ResponseEntity.ok(payService.getBalance(accountId).let(BalanceDetailResponse::from))
    }

    @PostMapping("/payment")
    fun processPayment(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
    ): ResponseEntity<Void> {
        payService.processPayment(accountId)
        return ResponseEntity.noContent().build()
    }
}