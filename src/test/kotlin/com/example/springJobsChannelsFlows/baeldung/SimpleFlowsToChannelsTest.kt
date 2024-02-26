package com.example.springJobsChannelsFlows.baeldung

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.produceIn
import org.junit.jupiter.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest

//@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SimpleFlowsToChannelsTest {

    private val log: Logger = LoggerFactory.getLogger(SimpleFlowsToChannelsTest::class.java)

    @Test
    @Order(1)
    fun `flow to channels test`() = runBlocking{
        val channel = receiveChannel()
        val result = deferred(channel)
        Assertions.assertEquals(result.await(), "1, 2, 3, 4, 5")
    }

    @Test
    @Order(2)
    fun `flow to channels with capacity`() = runBlocking{
        val myChannel = gimmeChannelFromFlow(10)
        val result = processFlow(myChannel)
        Assertions.assertEquals(result.await(), "1, 2, 3, 4, 5")
    }

    private fun CoroutineScope.receiveChannel(): ReceiveChannel<Int> {
        val channel = flow {
            for (i in 1..5) {
                delay(100)
                emit(i)
            }
        }.buffer(
            capacity = 2,
            onBufferOverflow = BufferOverflow.SUSPEND
        ).produceIn(this)
        return channel
    }

    private fun CoroutineScope.deferred(channel: ReceiveChannel<Int>): Deferred<String> {
        val result = async {
            buildString {
                channel.consumeEach {
                    append(it).append(", ")
                }
            }.removeSuffix(", ")
        }
        return result
    }
}
private fun CoroutineScope.gimmeChannelFromFlow(size:Int): ReceiveChannel<Int> {
    val channel =    flow {

        for (i in 1..5) {
            delay(100)
            emit(i)
        }
    }.buffer(
        capacity = size,
        onBufferOverflow = BufferOverflow.SUSPEND
    ).produceIn(this)
    return channel

}



private fun CoroutineScope.processFlow(channel: ReceiveChannel<Int>): Deferred<String> {
    val result = async {
        buildString {
            channel.consumeEach {
                append(it).append(", ")
            }
        }.removeSuffix(", ")
    }
    return result
}
