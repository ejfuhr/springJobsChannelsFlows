package com.example.springJobsChannelsFlows.baeldung

import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

/**
 * from https://www.baeldung.com/kotlin/flows-vs-channels
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BaeldungFlowsChannelsTest {

    @Test
    @Order(1)
    fun when_collected_flow_multiple_time_then_return_same_values() = runBlocking {
        val coldStream = flow {
            for (i in 1..5) {
                delay(100L)
                emit(i)
            }
        }
        val collect1 = buildString {
            coldStream.collect { append(it).append(", ") }
        }.removeSuffix(", ")
        val collect2 = buildString {
            coldStream.collect { append(it).append(", ") }
        }.removeSuffix(", ")
        assertEquals("1, 2, 3, 4, 5", collect1)
        assertEquals("1, 2, 3, 4, 5", collect2)
    }

    @Test
    @Order(2)
    fun given_channel_when_pass_data_from_one_coroutine_then_receive_in_another() = runBlocking {
        val channel = Channel<Int>()
        launch { // coroutine #1
            for (i in 1..5) {
                delay(100L)
                channel.send(i)
            }
            channel.close()
        }
        val result = async { // coroutine #2
            buildString {
                channel.consumeEach {
                    append(it).append(", ")
                }
            }.removeSuffix(", ")
        }
        assertEquals("1, 2, 3, 4, 5", result.await())
    }

    @Test
    @Order(3)
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun when_use_produce_then_consumeEach_receives_all_values() = runBlocking {
        val channel = Channel<Int>()

        produce<Int> {
            for (i in 1..5) {
                channel.send(i)
            }
            channel.close()
        }

        val result = async {
            buildString {
                channel.consumeEach {
                    append(it).append(", ")
                }
            }.removeSuffix(", ")
        }

        assertEquals(result.await(), "1, 2, 3, 4, 5")
    }

    @Test
    @Order(4)
    fun when_flow_produceIn_then_consume_all_values() = runBlocking {
        val channel = flow {
            for (i in 1..5) {
                delay(100)
                emit(i)
            }
        }.buffer(
            capacity = 2,
            onBufferOverflow = BufferOverflow.SUSPEND
        ).produceIn(this)

        val result = async {
            buildString {
                channel.consumeEach {
                    append(it).append(", ")
                }
            }.removeSuffix(", ")
        }

        assertEquals(result.await(), "1, 2, 3, 4, 5")
    }

}