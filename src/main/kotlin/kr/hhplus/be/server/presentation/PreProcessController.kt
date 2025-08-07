package kr.hhplus.be.server.presentation

import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.Reservation
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.point.Balance
import kr.hhplus.be.server.domain.point.BalanceRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/**
초기 데이터 세팅을 위한 api로 아키텍처를 파괴했지만 최대한 코드 양을 줄이기 위한 선택이었습니다 ㅎㅎ (프로덕션 코드가 아니기에)
 **/
@RestController
@RequestMapping("/pre-process")
class PreProcessController(
    private val concertRepository: ConcertRepository,
    private val reservationRepository: ReservationRepository,
    private val balanceRepository: BalanceRepository,
) {
    @PostMapping("")
    fun preProcess(): ResponseEntity<Void> {

        concertRepository.save(
            Concert(
                name = "펜타포트 락 페스티벌",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10),
            )
        )
        (1..50).forEach {
            reservationRepository.save(
                Reservation(
                    concertId = 1,
                    seatNo = it,
                    date = LocalDate.now(),
                )
            )
            reservationRepository.save(
                Reservation(
                    concertId = 1,
                    seatNo = it,
                    date = LocalDate.now().plusDays(1),
                )
            )
        }
        balanceRepository.save(Balance(accountId = "account1", amount = 1000L))

        return ResponseEntity.noContent().build()
    }
}