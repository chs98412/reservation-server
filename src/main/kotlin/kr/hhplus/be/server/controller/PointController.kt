package kr.hhplus.be.server.controller

import kr.hhplus.be.server.controller.model.request.BalanceChargeRequest
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

}