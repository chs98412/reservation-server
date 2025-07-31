package kr.hhplus.be.server.application.point

interface ProcessPaymentUseCase {
    fun execute(accountId: String)
}