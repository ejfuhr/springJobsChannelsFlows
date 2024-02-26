package com.example.springJobsChannelsFlows.baeldung

import com.example.springJobsChannelsFlows.entity.InspectorRepository
import com.example.springJobsChannelsFlows.entity.TruckRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.internal.configuration.CaptorAnnotationProcessor.process
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.coroutines.coroutineContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TruckInspectorFlowChannelsTest(
    @Autowired
    private val truckRepo: TruckRepository,
    @Autowired
    private val inspectorRepo: InspectorRepository
) {

    private val log: Logger = LoggerFactory.getLogger(TruckInspectorFlowChannelsTest::class.java)

    @Test
    @Order(1)
    fun `flow of inspectors`() = runBlocking {
        val list = inspectorRepo.findAll()
            .toList()
        for (i in list) {
            assertThat(i.specId).isGreaterThan(1)
            println("$i")
        }
        val myChannel = inspectorRepo.findAll()
            .buffer(
                capacity = 2,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
            .produceIn(this)

        for (i in myChannel) {
            delay(100L)
            myChannel.consumeEach {
                println("here's inspector ${i.specId} name ${i.name}")
            }
        }
        val list2 = flow{
            for (insp in inspectorRepo.findAll().toList()){
                delay(50)
                emit(insp)
            }
        }.buffer(
            capacity = 3,
            onBufferOverflow = BufferOverflow.SUSPEND
        ).produceIn(this)

        val result = async {
            list2.consumeEach {
                println("consuming each $it")
            }
        }
        result.await()
    }


    @Test
    @Order(10)
    fun `flow to channels test`() = runBlocking {
        val channel = receiveChannel()
        val result = deferred(channel)
        assertEquals(result.await(), "1, 2, 3, 4, 5")
    }

    @Test
    @Order(11)
    fun `flow to channels with capacity`() = runBlocking {
        val myChannel = gimmeChannelFromFlow(10)
        val result = processFlow(myChannel)
        assertEquals(result.await(), "1, 2, 3, 4, 5")
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

private fun CoroutineScope.gimmeChannelFromFlow(size: Int): ReceiveChannel<Int> {
    val channel = flow {

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