package kr.hhplus.be.server.infrastructure

import org.redisson.api.RedissonClient
import java.util.concurrent.TimeUnit

inline fun <T> RedissonClient.withLock(
    key: String,
    waitTime: Long = 5,
    leaseTime: Long = 10,
    unit: TimeUnit = TimeUnit.SECONDS,
    fair: Boolean = false,
    onFail: () -> T = {
        throw LockAcquireTimeoutException(
            "Timeout while acquiring lock for key='$key' (wait=${waitTime} ${unit.name.lowercase()})"
        )
    },
    block: () -> T
): T {
    val rlock = if (fair) getFairLock(key) else getLock(key)
    val acquired = runCatching {
        rlock.tryLock(waitTime, leaseTime, unit)
    }.getOrElse { e ->
        throw LockAcquireException("Failed to tryLock for key='$key'", e)
    }
    if (!acquired) return onFail()

    try {
        return block()
    } finally {
        if (rlock.isHeldByCurrentThread) {
            runCatching { rlock.unlock() }
        }
    }
}

inline fun <T> RedissonClient.acquireLockOrThrow(
    key: String,
    leaseTime: Long = 10,
    unit: TimeUnit = TimeUnit.SECONDS,
    fair: Boolean = false,
    onFail: () -> T = {
        throw LockAcquireTimeoutException("Immediate lock acquisition failed for key='$key'")
    },
    block: () -> T
): T = withLock(
    key = key,
    waitTime = 0,
    leaseTime = leaseTime,
    unit = unit,
    fair = fair,
    onFail = onFail,
    block = block
)

class LockAcquireException(message: String, cause: Throwable) : RuntimeException(message, cause)
class LockAcquireTimeoutException(message: String) : RuntimeException(message)
