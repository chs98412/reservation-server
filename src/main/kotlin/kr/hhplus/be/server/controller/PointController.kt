package kr.hhplus.be.server.controller

import kr.hhplus.be.server.controller.model.response.PointFetchResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/point")
class PointController {
    @GetMapping("{id}")
    fun fetchPoint(
        @PathVariable id: Long,
    ): ResponseEntity<PointFetchResponse> {
        return ResponseEntity.ok(PointFetchResponse.mockResponse)
    }
}