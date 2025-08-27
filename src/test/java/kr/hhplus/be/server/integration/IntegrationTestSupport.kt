package kr.hhplus.be.server.integration

import TestRedissonConfig
import com.redis.testcontainers.RedisContainer
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@Import(TestRedissonConfig::class)
abstract class IntegrationTestSupport {
    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val redis: RedisContainer = RedisContainer(
            DockerImageName.parse("redis:7.2-alpine")
        ).apply {
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.redis.host") { redis.host }
            registry.add("spring.redis.port") { redis.firstMappedPort }
        }
    }
}
