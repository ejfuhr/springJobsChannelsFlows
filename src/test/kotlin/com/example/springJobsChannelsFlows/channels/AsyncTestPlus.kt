package com.example.springJobsChannelsFlows.channels

import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * https://discuss.kotlinlang.org/t/coroutines-concurrency-in-a-loop/22297
 */
class AsyncTestPlus {

    @Test
    fun performanceAsync2() = runBlocking {
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
    }
}

private suspend inline fun doHardStuff(value: Int): String {
    println("${Thread.currentThread().name}: $value")
    delay(1000L)
    return value.toString()
}