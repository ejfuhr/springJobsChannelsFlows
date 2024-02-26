package com.example.springJobsChannelsFlows.channels

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.milliseconds

//@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ChannelFlowsSimpleTest {

    private val log: Logger = LoggerFactory.getLogger(ChannelFlowsSimpleTest::class.java)

    @Test
    @Order(1)
    fun `test channels Flows Int`() = runTest {
        mainStuffAsFlow().collect {
            assertThat(it).isNotNull
            println("print it here:: $it ")
        }
    }

    @Test
    @Order(2)
    fun `test channelFlow `() = runTest {
        val mylist = mutableListOf<String>()
        val words = channelFlow {
            launch { send("Hello") }
            launch { send("World!") }
            launch { send("World no2!") }
        }
        println(words.toList(mylist).joinToString(","))
        assertNotNull(words)
    }

    @Test
    @Order(3)
    fun `test channels from jobs`() = runTest {
        doFlowsHotColdLast()
    }

    suspend fun doFlowsHotColdLast() = coroutineScope {
        val channel = Channel<String>()
        val channel2 = Channel<String>()
        coroutineScope {
            // You can't do this with a flow!
            launch {
                channel.send("Hello from job 1!")
                println("Job 1 received a message: ${channel.receive()}")
            }
            launch {
                println("Job 2 received a message: ${channel.receive()}")
                channel.send("Hello from job 2!")
                println("Job2 received a message: ${channel2.receive()}")
            }
            launch{
                println("in last launch...")
                println("channel:: ${channel.isEmpty} ${channel.isClosedForReceive} ${channel.isClosedForSend}")
                //channel2.send("lemmen get a channel.receive ${channel.receive()}")
                //val message = channel.receive()
                channel2.send("from Job 3")
//                channel2.send("another from Job 3")
//                channel2.receive()
                //println("receiving channel 2 ${channel2.receive()}")
                println("channel2:: ${channel2.isEmpty} ${channel2.isClosedForReceive} ${channel2.isClosedForSend}")
            }

        }

        channel.close()//close outside of scope??
        channel2.close()

    }

    fun <T> buildFlowWithChannel(builder: CoroutineScope.(Channel<T>) -> Unit) = flow {
        coroutineScope {
            val output = Channel<T>()
            builder(output)
            emitAll(output)
        }
    }

    fun mainStuffAsFlow(): Flow<Any> {
        val channel1 = Channel<Int>()
        val channel2 = Channel<Int>()
        val channel3 = Channel<String>()


        val flow1 = channel1.consumeAsFlow()
        val flow2 = channel2.consumeAsFlow()
        val flow3 = channel3.consumeAsFlow()
        val flow4FromFlow = flowOf("Hello World", "wow")

        val mergedFlow = mergeFlows(flow1, flow2, flow3, flow4FromFlow)

        // Launch a coroutine to send data to the channels
        CoroutineScope(Dispatchers.Default).launch {
            repeat(10) {
                channel1.send(it)
                channel2.send(it * 10)
                channel3.send("sending it as string: $it")
                delay(100L) // simulate some delay
            }
            channel1.close()
            channel2.close()
            channel3.close()
        }

        // Collect the merged flow
        return mergedFlow
        //mergedFlow.collect { println("print it here:: $it ") }
    }


}


fun <T> mergeFlows(vararg flows: Flow<T>): Flow<T> = channelFlow {
    flows.forEach { flow ->
        launch {
            flow.collect { send(it) }
        }
    }
}