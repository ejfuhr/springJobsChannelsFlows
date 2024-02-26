package com.example.springJobsChannelsFlows.promises


import io.netty.util.concurrent.Promise
import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories.future
import org.junit.jupiter.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.*

//@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class Promises1Test {

    private val log: Logger = LoggerFactory.getLogger(Promises1Test::class.java)

    @Test
    @Order(1)
    fun `test promise from CompleteableFuture`(){

        val promise = CompletableFuture<String>()

        Thread {
            // Simulate an expensive operation
            Thread.sleep(1000)
            promise.complete("Hello, world!")
        }.start()

        promise.thenAccept { result ->
            println("Async operation completed with result: $result")
            assertThat(result).isEqualTo("Hello, world!")
        }

        println("Waiting for async operation to complete...")
        Thread.sleep(2000)
    }

    @Test
    @Order(2)
    fun `test Promise as Promise`() = runBlocking<Unit> {
        launch(Dispatchers.Default + CoroutineName("test")) {
            println("I'm working in thread ${Thread.currentThread().name}")
        }
    }

}