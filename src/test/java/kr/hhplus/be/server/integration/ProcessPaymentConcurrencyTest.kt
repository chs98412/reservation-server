package kr.hhplus.be.server.integration

import io.kotest.matchers.shouldBe
import kr.hhplus.be.server.application.point.ProcessPaymentService
import kr.hhplus.be.server.domain.concert.Reservation
import kr.hhplus.be.server.domain.concert.ReservationRepository
import kr.hhplus.be.server.domain.concert.Status
import kr.hhplus.be.server.domain.point.Balance
import kr.hhplus.be.server.domain.point.BalanceRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.OptimisticLockingFailureException
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class ProcessPaymentConcurrencyTest {

    @Autowired
    private lateinit var processPaymentService: ProcessPaymentService

    @Autowired
    private lateinit var balanceRepository: BalanceRepository

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    private val accountId = "test-user"

    @BeforeEach
    fun setUp() {
        reservationRepository.deleteAll()
        balanceRepository.deleteAll()

        balanceRepository.save(Balance(accountId = accountId, amount = 10_000))

        reservationRepository.saveAll(
            listOf(
                Reservation(
                    concertId = 1L,
                    seatNo = 1,
                    status = Status.RESERVED,
                    accountId = accountId,
                    price = 3_000,
                    date = LocalDate.now()
                ),
                Reservation(
                    concertId = 1L,
                    seatNo = 2,
                    status = Status.RESERVED,
                    accountId = accountId,
                    price = 3_000,
                    date = LocalDate.now()
                ),
            )
        )
    }

    @Test
    fun `동시에 결제를 요청하면 하나만 성공하고 나머지는 실패해야 한다`() {
        val threadCount = 5
        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        val successList = Collections.synchronizedList(mutableListOf<Int>())
        val failedList = Collections.synchronizedList(mutableListOf<Int>())

        repeat(threadCount) { idx ->
            executor.submit {
                try {
                    processPaymentService.execute(accountId)
                    successList.add(idx)
                } catch (e: OptimisticLockingFailureException) {
                    failedList.add(idx)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        successList.size shouldBe 1
        failedList.size shouldBe threadCount - 1

        val balance = balanceRepository.findByAccountId(accountId)!!
        balance.amount shouldBe 4000

    }
}
