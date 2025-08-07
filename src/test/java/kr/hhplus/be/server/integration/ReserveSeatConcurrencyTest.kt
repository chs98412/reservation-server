package kr.hhplus.be.server.integration

import io.kotest.matchers.shouldBe
import kr.hhplus.be.server.application.concert.ReserveSeatService
import kr.hhplus.be.server.application.concert.SeatReservationCommand
import kr.hhplus.be.server.domain.concert.Reservation
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.concert.Status
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.orm.ObjectOptimisticLockingFailureException
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class ReserveSeatConcurrencyTest {

    @Autowired
    private lateinit var reserveSeatService: ReserveSeatService

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @BeforeEach
    fun setUp() {
        reservationRepository.deleteAll()
    }

    @Test
    fun `동시에 같은 좌석을 예약하면 하나만 성공하고 나머지는 실패해야 한다`() {
        val concertId = 1L
        val seatNo = 1
        val accountIds = listOf("accountId1", "accountId2", "accountId3", "accountId4", "accountId5")

        reservationRepository.save(
            Reservation(
                concertId = concertId,
                seatNo = seatNo,
                status = Status.AVAILABLE,
                accountId = null,
                date = LocalDate.now()
            )
        )

        val executor = Executors.newFixedThreadPool(accountIds.size)
        val latch = CountDownLatch(accountIds.size)
        val succeedResults = Collections.synchronizedList(mutableListOf<String>())
        val failedResults = Collections.synchronizedList(mutableListOf<String>())

        accountIds.forEach { accId ->
            executor.submit {
                try {
                    reserveSeatService.execute(
                        SeatReservationCommand(
                            concertId = concertId,
                            seatNo = seatNo,
                            accountId = accId
                        )
                    )
                    succeedResults.add(accId)
                } catch (e: ObjectOptimisticLockingFailureException) {
                    failedResults.add(accId)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        succeedResults.size shouldBe 1
        failedResults.size shouldBe accountIds.size - 1
    }
}
