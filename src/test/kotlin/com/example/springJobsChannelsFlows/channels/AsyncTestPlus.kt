package com.example.springJobsChannelsFlows.channels

import com.example.springJobsChannelsFlows.controller.TruckInspectorController
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * https://discuss.kotlinlang.org/t/coroutines-concurrency-in-a-loop/22297
 */
class AsyncTestPlus {

    private val log: Logger = LoggerFactory.getLogger(AsyncTestPlus::class.java)

    @Test
    fun performanceAsync2() = runTest {
        val count = 20
        val list = mutableListOf<String>()
        val d1 = mutableListOf<Deferred<String>>()
        val d2 = mutableListOf<Deferred<String>>()

        repeat(count) {
            d1.add(async(Dispatchers.Default) { doHardStuff(it * 2) })
            d2.add(async(Dispatchers.Default) { doHardStuff(it * 2 + 1) })
        }
        repeat(count) {
            list.add(it * 2, d1.awaitAll()[it])
            list.add(it * 2 + 1, d2.awaitAll()[it])
        }
        assertEquals((0 until count * 2).joinToString(), list.joinToString())
        log.info("d1 here here ${d1.joinToString()}")
        log.info("d2 here here ${d2.joinToString()}")
    }
}

private suspend inline fun doHardStuff(value: Int): String {
    println("${Thread.currentThread().name}: $value")
    delay(1000L)
    return value.toString()
}