package kr.hhplus.be.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@EnableJpaRepositories("kr.hhplus.be.server.domain")
@SpringBootApplication
@EnableScheduling
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}