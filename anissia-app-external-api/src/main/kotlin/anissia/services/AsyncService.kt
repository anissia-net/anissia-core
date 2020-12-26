package anissia.services

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AsyncService {
    @Async
    fun async(execute: () -> Unit): Unit = execute()
}