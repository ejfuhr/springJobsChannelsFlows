package com.example.springJobsChannelsFlows.entity

import com.example.springJobsChannelsFlows.controller.TruckInspectorController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val log: Logger = LoggerFactory.getLogger(TruckInspectorController::class.java)
val queueSize = 2
val sleep: Long = (400..800).random().toLong()

/*
 * the following three functions 1) queueTrucks, 2) filterTrucksReport then 3) SendChannel inspectOutput
 * they should 1) queueTrucks returning ReceiveChannel<Truck>
 * then 2) checkTires returning ReceiveChannel<Truck>
 * then 3) inspectAgricultureCriminalBehavior returning ReceiveChannel<Truck>
 * then 4) do something similar to inspectOutput but return a combined Truck Inspector with clean/failed inspection
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
fun CoroutineScope.queueTrucks(truckList: MutableList<Truck>, report: InspectionReport)
        : ReceiveChannel<Truck> = produce(capacity = queueSize) {
    println("Source starting")
    report.notes.add("Source starting")
    for (i in truckList) {
        channel.send(i)
        println("Source iteration sent $i")
        report.notes.add("Source iteration sent $i")
        delay(sleep)
    }
    channel.close() // why close??
    println("Source exiting")
    report.notes.add("Source exiting")
}

/**
 * this function should handle trucks, inspectors and report
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
fun CoroutineScope.filterTrucksReport(trucks: ReceiveChannel<Truck>, report: InspectionReport)
        : ReceiveChannel<InspectionReport> = produce(capacity = queueSize) {
    println("  Filter starting")
    for (x in trucks) {
        println("  Filter received $x")
        delay(sleep)
        val y = "'$x'"
        //report.notes.add(y)
        report.notes.add("  Filter sent $y")
        channel.send(report)
        println("  Filter sent $y")
    }
    channel.close() //why close??
    println("  Filter exiting")
    report.notes.add("  Filter exiting")
}
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
fun CoroutineScope.inspectOutput(report: InspectionReport)
        : SendChannel<InspectionReport> = actor(capacity = 2) {
    println("    Output starting")
    for (x in channel) {
        report.notes.add("    Output received $x")
        println("    Output received $x")

        delay(sleep)

        //report.notes.add("here is complete channel.receive() " + channel.receive())
    }
    println("    Output exiting")
    report.notes.add("    Output exiting")
}