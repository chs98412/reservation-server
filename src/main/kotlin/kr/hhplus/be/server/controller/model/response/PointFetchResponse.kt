package kr.hhplus.be.server.controller.model.response

import org.springframework.http.ResponseEntity

data class PointFetchResponse(
    val id: Long,
    val point: Long,
){
    companion object {
        val mockResponse= PointFetchResponse(id=1, point=1000)
    }
}
