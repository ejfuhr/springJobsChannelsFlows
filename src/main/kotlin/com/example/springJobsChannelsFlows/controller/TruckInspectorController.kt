package com.example.springJobsChannelsFlows.controller

import com.example.springJobsChannelsFlows.entity.*
import com.example.springJobsChannelsFlows.service.InspectorService
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.Flow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import kotlin.time.Duration.Companion.seconds


/**
 * this Controller meant to mix  source, filter, output
 * as in FVascoThreadsQueuesTest
 */
@RestController
@RequestMapping("/inspect")
class TruckInspectorController(
    @Autowired
    val variousNotesRepo: VariousNotesRepository,
    @Autowired
    val truckRepo: TruckRepository,
    @Autowired
    val inspectorRepo: InspectorRepository,
    @Autowired
    val reportRepo: InspectionReportRepository,
    @Autowired
    val spectionRepo: DailyAllReportRepository,
    @Autowired
    private val inspectorService:InspectorService
) {

    private val log: Logger = LoggerFactory.getLogger(TruckInspectorController::class.java)
    private val queueSize = 2

    @GetMapping("/reports/all")
    fun getAllInspectionReports(): Flow<DailyInspection> {
        return spectionRepo.findAll()
    }
    @GetMapping(path = ["/trucks/output/report-id/{reportSpecId}/truck-ids/{specIdsList}/spector-ids/{inspectorIdsList}"],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun trucksOutputFromIds(
        @PathVariable reportSpecId: Int,
        @PathVariable specIdsList: List<Int>,
        @PathVariable inspectorIdsList:List<Int>
    ): InspectionReport = runBlocking<InspectionReport> {
        // if reort is NOT null, delete it
        if(reportRepo.findBySpecId(reportSpecId) != null){
            reportRepo.delete(reportRepo.findBySpecId(reportSpecId))
        }
        //create report new

        //val report: VariousNotes = VariousNotes(null, reportSpecId, mutableListOf())
        val truckList: MutableList<Truck> = mutableListOf()
        val inspectorList : MutableList<Inspector> = mutableListOf()
        for (specId in specIdsList) {
            log.info("found specId $specId")
            truckList.add(truckRepo.findBySpecId(specId))
        }
        for(inspectId in inspectorIdsList){
            log.info("found inspectId $inspectId")
            inspectorList.add(inspectorRepo.findBySpecId(inspectId))
        }
        val report = InspectionReport(null,
            specId = reportSpecId,
            currentDate = LocalDate.now(),
            trucksInLine = truckList,
            inspectors = inspectorList
            )
        log.info("pre mainThing")

        //mainThing(truckList)
        val channelTruck  = Channel<Truck>()
        coroutineScope {

            val source = queueTrucks(truckList, report)
            val filter = filterTrucksReport(source, report)
            val output = inspectOutput(report)
            filter.consumeEach {
                reportRepo.save(it)
                output.send(it)
            }
            output.close()
        }
        return@runBlocking reportRepo.save(report)
    }
//truckList: MutableList<Truck>

    @GetMapping(path = ["/trucks/output/notes-specId/{specId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    //@RequestMapping
    fun doTrucksOutput(@PathVariable specId: Int): VariousNotes = runBlocking<VariousNotes> {
        // uses truckSpec nos 10101, 10102, 10103

        /* from runALl - we want a full report VariousNotes
                println("runAll starting")
                here is copy/variation of runAll()

         */
        if (variousNotesRepo.findBySpecId(specId) != null) {
            variousNotesRepo.delete(variousNotesRepo.findBySpecId(specId))
        }
        val notes: VariousNotes = VariousNotes(null, specId, mutableListOf())
        val truck1 = truckRepo.findBySpecId(10101)
        val truck2 = truckRepo.findBySpecId(10102)
        val truck3 = truckRepo.findBySpecId(10103)
        val truckList = mutableListOf(truck1, truck2, truck3)
        println("runAll starting")
        notes.notes.add("runAll starting")
        coroutineScope {

            val source = sourceTrucks(truckList, notes)
            val filter = filterTrucks(source, notes)
            val output = output(notes)
            filter.consumeEach { output.send(it) }
            output.close()
        }
        notes.notes.add("runAll exiting")
        return@runBlocking variousNotesRepo.save(notes)
    }
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun CoroutineScope.sourceTrucks(truckList: MutableList<Truck>, notes: VariousNotes)
            : ReceiveChannel<Truck> = produce(capacity = queueSize) {
        println("Source starting")
        notes.notes.add("Source starting")
        for (i in truckList) {
            channel.send(i)
            println("Source iteration sent $i")
            notes.notes.add("Source iteration sent $i")
            val sleep: Long = (400..600).random().toLong()
            withContext(Dispatchers.IO) {
                Thread.sleep(sleep)
            }
        }
         channel.close() // why close??
        println("Source exiting")
        notes.notes.add("Source exiting")
    }
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun CoroutineScope.filterTrucks(trucks: ReceiveChannel<Truck>, notes: VariousNotes)
            : ReceiveChannel<VariousNotes> = produce(capacity = queueSize) {
        println("  Filter starting")
        for (x in trucks) {
            println("  Filter received $x")
            val sleep: Long = (400..600).random().toLong()
            withContext(Dispatchers.IO) {
                Thread.sleep(sleep)
            }
            val y = "'$x'"
            notes.notes.add(y)
            notes.notes.add("  Filter sent $y")
            channel.send(notes)
            println("  Filter sent $y")
        }
        channel.close() //why close??
        println("  Filter exiting")
        notes.notes.add("  Filter exiting")
    }
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun CoroutineScope.output(notes: VariousNotes)
            : SendChannel<VariousNotes> = actor(capacity = queueSize) {
        println("    Output starting")
        for (x in channel) {
            notes.notes.add("    Output received $x")
            println("    Output received $x")
            val sleep: Long = (400..600).random().toLong()
            withContext(Dispatchers.IO) {
                Thread.sleep(sleep)
            }
            //notes.notes.add("what?? " + channel.receive())
        }
        println("    Output exiting")
        notes.notes.add("    Output exiting")
    }
}


