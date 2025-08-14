package kr.hhplus.be.server

import RedisTestContainer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.application.concert.GetAvailableDatesService
import kr.hhplus.be.server.common.exception.NotFoundConcertException
import kr.hhplus.be.server.infrastructure.acquireLockOrThrow
import kr.hhplus.be.server.infrastructure.withLock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.redisson.api.RedissonClient
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@Testcontainers
class RedisTest {

    private lateinit var redisson: RedissonClient


    @BeforeEach
    fun setup() {
        redisson = RedisTestContainer.newClient()
    }

    @AnnotationSpec.AfterAll
    fun tearDown() {
        redisson.shutdown()
    }

    @Test
    fun `락 획득 테스트`() {
        val counter = AtomicInteger(0)

        val executor = Executors.newFixedThreadPool(10)

        repeat(10) {
            executor.submit {
                redisson.withLock("test:lock") {
                    counter.incrementAndGet()
                }
            }
        }

        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)

        assertEquals(10, counter.get())
    }

    @Test
    fun `락 획득 테스트 (따닥 방지용)`() {
        val counter = AtomicInteger(0)

        val executor = Executors.newFixedThreadPool(10)

        repeat(10) {
            executor.submit {
                redisson.acquireLockOrThrow("test:lock") {
                    counter.incrementAndGet()
                }
            }
        }

        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)

        assertEquals(1, counter.get())
    }

    @Test
    fun `락시도 함수(따닥 방지용)은 수행 함수의 예외를 그대로 반환한다`() {
        val getAvailableDatesService: GetAvailableDatesService = mockk<GetAvailableDatesService>()
        every { getAvailableDatesService.execute(any()) } throws NotFoundConcertException()

        shouldThrow<NotFoundConcertException> {
            redisson.acquireLockOrThrow("test:lock") {
                getAvailableDatesService.execute(1)
            }
        }
    }

    @Test
    fun `락시도 함수는 수행 함수의 예외를 그대로 반환한다`() {
        val getAvailableDatesService: GetAvailableDatesService = mockk<GetAvailableDatesService>()
        every { getAvailableDatesService.execute(any()) } throws NotFoundConcertException()

        shouldThrow<NotFoundConcertException> {
            redisson.withLock("test:lock") {
                getAvailableDatesService.execute(1)
            }
        }
    }
}