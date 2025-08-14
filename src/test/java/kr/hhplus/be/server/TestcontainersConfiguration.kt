import com.redis.testcontainers.RedisContainer
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class TestRedissonConfig {
    @Value("\${spring.redis.host}")
    lateinit var redisHost: String

    @Value("\${spring.redis.port}")
    lateinit var redisPort: String

    @Bean
    fun redissonClient(): RedissonClient {
        val address = "redis://$redisHost:$redisPort"
        val config = Config().apply {
            useSingleServer().address = address
        }
        return Redisson.create(config)
    }


}

@Testcontainers
object RedisTestContainer {
    private val container: RedisContainer = RedisContainer(
        DockerImageName.parse("redis:7.2-alpine")
    ).apply {
        start()
    }

    fun newClient(): RedissonClient {
        val address = "redis://${container.host}:${container.firstMappedPort}"
        val config = Config().apply { useSingleServer().address = address }
        return Redisson.create(config)
    }
}