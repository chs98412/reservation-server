package kr.hhplus.be.server.infrastructure

import kr.hhplus.be.server.application.dataPlatform.DataPlatformService
import org.springframework.stereotype.Component

@Component
class DataPlatformImpl : DataPlatformService {
    override fun sendData(data: Any) {
        //데이터 플랫폼 전송 호출부
        TODO("Not yet implemented")
    }
}