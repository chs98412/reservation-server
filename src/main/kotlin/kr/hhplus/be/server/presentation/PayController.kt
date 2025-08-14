package kr.hhplus.be.server.presentation

import kr.hhplus.be.server.application.point.*
import kr.hhplus.be.server.infrastructure.acquireLockOrThrow
import org.redisson.api.RedissonClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PayController(
    private val chargePointUseCase: ChargePointUseCase,
    private val getBalanceUseCase: GetBalanceUseCase,
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val redisson: RedissonClient,
) {

    @PostMapping("/charge")
    fun chargeBalance(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
        @RequestBody request: BalanceChargeRequest
    ): ResponseEntity<Void> {
        chargePointUseCase.execute(accountId, request.amount)
        return ResponseEntity.noContent().build()
    }

    @GetMapping
    fun getBalance(
        @RequestHeader("X-ACCOUNT-ID") accountId: String
    ): ResponseEntity<BalanceFetchResponse> {
        return ResponseEntity.ok(getBalanceUseCase.execute(accountId))
    }

    @PostMapping("/payment")
    fun processPayment(
        @RequestHeader("X-ACCOUNT-ID") accountId: String,
    ): ResponseEntity<Void> {
        redisson.acquireLockOrThrow(key = "pay:${accountId}") {
            processPaymentUseCase.execute(accountId)
        }
        return ResponseEntity.noContent().build()
    }
}