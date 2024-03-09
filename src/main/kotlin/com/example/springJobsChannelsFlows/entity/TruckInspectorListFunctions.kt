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
import java.time.LocalDate

private val log: Logger = LoggerFactory.getLogger(TruckInspectorController::class.java)

/* from TruckInspectorFunctions.kt
 * the following three functions 1) queueTrucks, 2) filterTrucksReport then 3) SendChannel inspectOutput
 * they should 1) queueTrucks returning ReceiveChannel<Truck>
 * then 2) checkTires returning ReceiveChannel<Truck>
 * then 3) inspectAgricultureCriminalBehavior returning ReceiveChannel<Truck>
 * then 4) do something similar to inspectOutput but return a combined Truck Inspector with clean/failed inspection
 */

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
fun CoroutineScope.queueTrucksInspectors(
    inspectors: MutableList<Inspector>,
    truckList: MutableList<Truck>,
    report: InspectionReport
)
        : ReceiveChannel<TruckInspectorList> = produce(capacity = queueSize) {
    val truckdao: TruckInspectorList = TruckInspectorList(
        null, 10101,
        LocalDate.now(), inspectors, truckList
    )
    report.notes.add("queueTrucksInspectors")
    channel.send(truckdao)
    report.notes.add("queueTrucksInspectors iteration sent ${truckdao.currentDate}")
    delay(sleep)
    channel.close() // why close??
    report.notes.add("queueTrucksInspectors exiting")
}

/**
 * this function should handle trucks, inspectors and report
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
fun CoroutineScope.checkTires(
    truckdao: ReceiveChannel<TruckInspectorList>,
    report: InspectionReport
)
        : ReceiveChannel<TruckInspectorList> = produce(capacity = queueSize) {
    println("  checkTires starting")
    for (item in truckdao) {
        println("  checkTires received $item")
        delay(sleep)
        report.inspectors += item.inspectorsList
        report.trucksInLine += item.truckList
        report.notes.add("  checkTires sent $item")
        channel.send(item)
        println("  checkTires sent $item")
    }
    channel.close() //why close??
    println("  checkTires exiting")
    report.notes.add("  checkTires exiting")
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
fun CoroutineScope.inspectFinish(report: InspectionReport)
        : SendChannel<InspectionReport> = actor(capacity = 2) {
    println("    inspectFinish starting")
    for (x in channel) {
        report.notes.add("    inspectFinish received $x")
        println("    inspectFinish received $x")
        delay(sleep)
    }
    println("    inspectFinish exiting")
    report.notes.add("    inspectFinish exiting")
}