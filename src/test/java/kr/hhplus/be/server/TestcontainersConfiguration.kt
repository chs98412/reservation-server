import kr.hhplus.be.server.infrastructure.acquireLockOrThrow
import kr.hhplus.be.server.infrastructure.withLock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@Testcontainers
class RedissonWithTestcontainersTest {

    companion object {
        @Container
        @JvmStatic
        val redis = GenericContainer(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379)
    }

    private lateinit var redisson: RedissonClient

    @BeforeEach
    fun setUp() {
        val addr = "redis://${redis.host}:${redis.firstMappedPort}"
        val config = Config().apply { useSingleServer().address = addr }
        redisson = Redisson.create(config)
    }

    @AfterEach
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

}
