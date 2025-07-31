package kr.hhplus.be.server.application.point

import kr.hhplus.be.server.application.point.BalanceFetchResponse
import kr.hhplus.be.server.common.exception.NotFoundBalanceException
import kr.hhplus.be.server.domain.point.BalanceRepository
import org.springframework.stereotype.Service

@Service
class GetBalanceService(
    private val balanceRepository: BalanceRepository,
) : GetBalanceUseCase {
    override fun execute(accountId: String): BalanceFetchResponse {
        return balanceRepository.findByAccountId(accountId)?.let(BalanceFetchResponse::from)
            ?: throw NotFoundBalanceException()
    }
}