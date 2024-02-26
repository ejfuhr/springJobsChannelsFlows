package com.example.springJobsChannelsFlows.channels

import com.example.springJobsChannelsFlows.entity.*
import com.example.springJobsChannelsFlows.promises.Promises1Test
import com.example.springJobsChannelsFlows.service.ChannelsPlusService
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ChannelsServiceTest(
    @Autowired
    private val channelsPlusService: ChannelsPlusService,
    @Autowired
    private val truckRepo: TruckRepository,
    @Autowired
    private val inspectorRepo: InspectorRepository
) {

    private val log: Logger = LoggerFactory.getLogger(ChannelsServiceTest::class.java)

    /**
     * selecting from channels
     * https://kotlinlang.org/docs/select-expression.html#selecting-from-channels
     */

    @Test
    @Order(1)
    fun `play with channels function`() {
        val str = channelsPlusService.twoProducerChannels()
        assertThat(str).isEqualTo("wow")
    }

    @Test
    @Order(2)
    fun `test Truck existence`() {
        runBlocking {
            log.info("in blocking")
            assertNotNull(truckRepo.findBySpecId(10102))
        }
    }

    /**
     * producer scope
     */
    @ExperimentalCoroutinesApi
    private fun CoroutineScope.produceTrucks(noList: MutableList<Int>) = produce<Truck> {
        val truckList: MutableList<Truck> = mutableListOf()
        for (n in noList) {
            truckList.add(truckRepo.findBySpecId(n))
        }
        while (truckList.isNotEmpty()) {
            var logTruck = send(truckList.iterator().next())
            log.info("truck::", logTruck)
            delay((100))
        }
    }

    private fun CoroutineScope.process2(id: Int, channel: ReceiveChannel<Truck>) = launch {
        for (msg in channel) {
            println("Processor #$id received $msg")
        }
    }

    @ExperimentalCoroutinesApi
    fun twoTruckProducerChannels(): String = runBlocking {

        val listOfIds = mutableListOf(10101, 10102, 10103, 10104)
        val producer = produceTrucks(listOfIds)
        repeat(5) {
            process2(it, producer)
            println("receive " + producer.receive())
        }

        delay(950)
        producer.cancel() // cancel producer coroutine and thus kill them all
        return@runBlocking "wow"
    }

    @Test
    @Order(3)
    fun `test first attempt at Truck producers`() {
        val str = twoTruckProducerChannels()
        assertThat(str).isEqualTo("wow")
    }

    @Test
    @Order(4)
    fun `test channel to flow`() {
        runBlocking {
            val noList = channelToFlow()
            assertNotNull(noList, "list is null")
            //assertThat(noList).size().isGreaterThan(2)
            log.info("list size:: ${noList.size}")
            for (n in noList) {
                log.info("n equals $n" )

            }

        }
    }

    @ExperimentalCoroutinesApi
    fun channelToFlow(): List<Int> = runBlocking {
        val words = flowOf("z", "z2", "z3", "z4")
        val produceChannel = produceNumbersHere()
        repeat(3) {
            launchFlowProcess(it, produceChannel)
        }
        val flow = produceChannel.consumeAsFlow()
        return@runBlocking flow.toList()
        //return@runBlocking flow
    }

    @ExperimentalCoroutinesApi
    private fun CoroutineScope.launchFlowProcess(id: Int, channel: ReceiveChannel<Int>) = launch {
        for (msg in channel) {
            println("processing $id with this msg $msg")
        }
    }

    @ExperimentalCoroutinesApi
    private fun CoroutineScope.produceNumbersHere() = produce<Int> {
        var x = 1 // start from 1
        while (x<10) {
            send(x++) // produce next
            delay(100) // wait 0.1s
        }
    }

}