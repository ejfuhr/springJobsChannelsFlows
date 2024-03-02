package com.example.springJobsChannelsFlows.service

import com.example.springJobsChannelsFlows.entity.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.seconds

@Service
class InspectorService(
    @Autowired
    val variousNotesRepo: VariousNotesRepository,
    @Autowired
    val truckRepo: TruckRepository,
    @Autowired
    val inspectorRepo: InspectorRepository,
    @Autowired
    val reportRepo: InspectionReportRepository,
    @Autowired
    val dailyRepo: DailyAllReportRepository,
) {

    private val log: Logger = LoggerFactory.getLogger(InspectorService::class.java)
    private val queueSize = 2

    private val sleep: Long = (400..800).random().toLong()
    private val secondsSleep: Double = (1..2).random().toDouble()

    fun doStuff(truckList: MutableList<Truck>, report: InspectionReport){

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.queueTruckLine(truckList: MutableList<Truck>, report: InspectionReport)
            : ReceiveChannel<Truck> = produce(capacity = queueSize) {
        for (i in truckList) {
            report.notes.add("queueing truck: ${i.specId}")
            channel.send(i)
            report.notes.add("Source iteration sent $i ")
            delay(sleep)
        }
        channel.close()
        log.info("Source exiting")
        report.notes.add("queue exiting")

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.inspectNo1(receiveTruck: ReceiveChannel<Truck>, report: InspectionReport)
            : ReceiveChannel<Truck> = produce(capacity = queueSize) {
        for (i in receiveTruck) {
            report.notes.add("inspecting truck: ${i.specId}")
            channel.send(i)
            report.notes.add("inspecting iteration sent $i ")
            delay(sleep)
        }
        channel.close()
        log.info("inspection exiting")
        report.notes.add("inspection queue exiting")
    }

    //@OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.inspectOutput(report: InspectionReport)
            : SendChannel<InspectionReport> = actor(capacity = queueSize) {
        log.info("    Output starting")
        for (x in channel) {
            report.notes.add("    Output received $x")
            println("    Output received $x")
            delay(sleep)
            //notes.notes.add("what?? " + channel.receive())
        }
        log.info("    Output exiting")
        report.notes.add("    Output exiting")
    }

}