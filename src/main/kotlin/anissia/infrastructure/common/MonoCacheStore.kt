package anissia.infrastructure.common

import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class MonoCacheStore<ID, T>(
    private val cacheTimeMillis: Long
) {
    private val store: MutableMap<ID, CacheWrapper<T>> = ConcurrentHashMap()

    fun find(id: ID, orElseUpdateAndGet: (ID) -> Mono<T>): Mono<T> =
        (store[id]?.data?: update(id, orElseUpdateAndGet(id)))

    fun update(id: ID, data: Mono<T>): Mono<T> =
        data.apply {
            store[id] = CacheWrapper(System.currentTimeMillis() + cacheTimeMillis, this.cache(Duration.ofMillis(cacheTimeMillis + 60000)))
        }

    fun remove(id: ID): Mono<T>? = store.remove(id)?.data
    fun clear(): Unit = store.clear()

    init {
        if (cacheTimeMillis <= 60000) {
            throw IllegalArgumentException("cacheTimeMillis must be greater than 60 seconds")
        } else if (cacheTimeMillis > (365L * 24 * 60 * 60000)) {
            throw IllegalArgumentException("cacheTimeMillis must be less than 365 days")
        }
    }

    private class CacheWrapper<T> (
        private val expireTimeMillis: Long,
        private val _data: Mono<T>
    ) {
        val data get(): Mono<T>? = if (expireTimeMillis >= System.currentTimeMillis()) _data else null
    }
}
