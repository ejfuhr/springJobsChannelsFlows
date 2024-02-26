package com.example.springJobsChannelsFlows.queues

import com.example.springJobsChannelsFlows.flows.ToyFlowTest
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * from https://gist.github.com/lpar/38b2442c5e43e43de5dcaa9dbf9b6f40
 * and previous to that
 * https://discuss.kotlinlang.org/t/using-threads-and-blocking-queues-in-kotlin/13418/3
 *
 * This is an example of a three-stage multithreaded processing pipeline in Kotlin,
 with blocking operations occurring in all three stages of the pipeline, implemented
 using Kotlin `Channel` and coroutine objects.

 source -> filter -> output

 Thread.sleep is used to simulate a blocking IO operation.
 */

//@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ThreadsQueuesTest {
    private val log: Logger = LoggerFactory.getLogger(ThreadsQueuesTest::class.java)
    @Test
    @Order(1)
    fun mainTest() {
        println("main starting")
        runAll()
        println("main exiting")
    }


}

suspend fun source(cout: Channel<Int>) {
    println("Source starting")
    for (i in 1..10) {
        val x = (0..255).random()
        cout.send(x)
        println("Source iteration $i sent $x")
        withContext(Dispatchers.IO) {
            val sleep: Long = (400..600).random().toLong()
            Thread.sleep(sleep)
        }
    }
    cout.close()
    println("Source exiting")
}

suspend fun filter(cin: Channel<Int>, cout: Channel<String>) {
    println("  Filter starting")
    for (x in cin) {
        println("  Filter received $x")
        withContext(Dispatchers.IO) {
            val sleep: Long = (400..600).random().toLong()
            Thread.sleep(sleep)
        }
        val y = "'$x'"
        cout.send(y)
        println("  Filter sent $y")
    }
    cout.close()
    println("  Filter exiting")
}

suspend fun output(cin: Channel<String>) {
    println("    Output starting")
    for (x in cin) {
        println("    Output received $x")
        withContext(Dispatchers.IO) {
            val sleep: Long = (400..600).random().toLong()
            Thread.sleep(sleep)
        }
    }
    println("    Output exiting")
}

private const val queueSize = 2

fun runAll() {
    runBlocking {
        println("runAll starting")
        val pipe1 = Channel<Int>(queueSize)
        val pipe2 = Channel<String>(queueSize)
        GlobalScope.launch {
            launch { source(pipe1) }
            launch { filter(pipe1, pipe2) }
            launch { output(pipe2) }
        }.join()
    }
    println("runAll exiting")
}

