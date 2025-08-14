package kr.hhplus.be.server.infrastructure

import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

inline fun <T> RedissonClient.withLock(
    key: String,
    waitTime: Long = 5,
    leaseTime: Long = 10,
    unit: TimeUnit = TimeUnit.SECONDS,
    onFail: Throwable = LockAcquireException("락 소지에 실패했습니다. key='$key'"),
    block: () -> T
): T {
    val logger = LoggerFactory.getLogger(this::class.java)
    val rlock = getLock(key)
    val acquired = runCatching {
        rlock.tryLock(waitTime, leaseTime, unit)
    }.getOrElse { e ->
        Thread.currentThread().interrupt()
        logger.error(e.message, e)
        false
    }
    if (!acquired) {
        throw onFail
    }
    try {
        return block()
    } finally {
        if (rlock.isHeldByCurrentThread) {
            runCatching {
                rlock.unlock()
            }.getOrElse { e ->
                logger.error(e.message, e)
            }
        }
    }
}

inline fun <T> RedissonClient.acquireLockOrThrow(
    key: String,
    leaseTime: Long = 10,
    unit: TimeUnit = TimeUnit.SECONDS,
    onFail: Throwable = LockAcquireException("중복된 요청입니다."),
    block: () -> T
): T = withLock(
    key = key,
    waitTime = 0,
    leaseTime = leaseTime,
    unit = unit,
    onFail = onFail,
    block = block
)

class LockAcquireException(message: String) : RuntimeException(message)
