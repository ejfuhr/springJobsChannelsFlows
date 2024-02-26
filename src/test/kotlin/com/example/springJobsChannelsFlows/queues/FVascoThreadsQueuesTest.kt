package com.example.springJobsChannelsFlows.queues

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * from https://gist.github.com/lpar/38b2442c5e43e43de5dcaa9dbf9b6f40
 * and previous to that
 * https://discuss.kotlinlang.org/t/using-threads-and-blocking-queues-in-kotlin/13418/3
 *

 */

private const val queueSize = 2

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class FVascoThreadsQueuesTest  {
    private val log: Logger = LoggerFactory.getLogger(FVascoThreadsQueuesTest::class.java)

    @Test
    @Order(1)
    //@Disabled
    fun `test One`() = runTest {
        println("main starting")
        //runAll()
        coroutineScope {
            val source = source()
            val filter = filter(source)
            val output = output()

            filter.consumeEach { output.send(it) }
            output.close()
        }
        println("runAll exiting")
        println("main exiting")
    }

    @Test
    @Order(2)
    //@Disabled
    fun mainTest() = runBlocking{
        println("main starting")
        runAll()
        println("main exiting")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.source(): ReceiveChannel<Int> = produce(capacity = queueSize) {
        println("Source starting")
        for (i in 1..10) {
            channel.send(i)
            println("Source iteration sent $i")
            val sleep: Long = (400..600).random().toLong()
            withContext(Dispatchers.IO) {
                println("delaying $sleep")
                delay(sleep)

                //Thread.sleep(sleep)
            }
        }
        // channel.close()
        println("Source exiting")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.filter(numbers: ReceiveChannel<Int>): ReceiveChannel<String> = produce(capacity = queueSize) {
        println("  Filter starting")
        for (x in numbers) {
            println("  Filter received $x")
            val sleep: Long = (400..600).random().toLong()
            withContext(Dispatchers.IO) {
                Thread.sleep(sleep)
            }
            val y = "'$x'"
            channel.send(y)
            println("  Filter sent $y")
        }
        // channel.close()
        println("  Filter exiting")
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.output(): SendChannel<String> = actor(capacity = queueSize) {
        println("    Output starting")
        for (x in channel) {
            println("    Output received $x")
            val sleep: Long = (400..600).random().toLong()
            withContext(Dispatchers.IO) {
                Thread.sleep(sleep)
            }
        }
        println("    Output exiting")
    }

    suspend fun runAll() {
        println("runAll starting")
        coroutineScope {
            val source = source()
            val filter = filter(source)
            val output = output()

            filter.consumeEach { output.send(it) }
            output.close()
        }
        println("runAll exiting")
    }


}