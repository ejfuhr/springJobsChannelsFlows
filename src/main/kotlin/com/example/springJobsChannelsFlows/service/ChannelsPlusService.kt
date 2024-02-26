package com.example.springJobsChannelsFlows.service

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service

@Service
class ChannelsPlusService {

    /** from channels-no6
     * Multiple coroutines may receive from the same channel, distributing work between themselves.
     * Let us start with a producer coroutine that is periodically producing integers (ten numbers per second):
    Then we can have several processor coroutines. In this example, they just print their id and received number
    Now let us launch five processors and let them work for almost a second. See what happens:
     */
    @ExperimentalCoroutinesApi
    private fun CoroutineScope.produceNumbers2() = produce<Int> {
        var x = 1 // start from 1
        while (true) {
            send(x++) // produce next
            delay(100) // wait 0.1s
        }
    }

    private fun CoroutineScope.launchProcessor2(id: Int, channel: ReceiveChannel<Int>) = launch {
        for (msg in channel) {
            println("Processor #$id received $msg")
        }
    }

    fun twoProducerChannels(): String = runBlocking {

        val producer = produceNumbers2()
        repeat(5) { launchProcessor2(it, producer) }
        delay(950)
        producer.cancel() // cancel producer coroutine and thus kill them all
        return@runBlocking "wow"
    }


}