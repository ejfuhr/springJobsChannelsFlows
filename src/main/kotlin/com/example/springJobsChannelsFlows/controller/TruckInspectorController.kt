package com.example.springJobsChannelsFlows.controller

import com.example.springJobsChannelsFlows.entity.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


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
    val inspectorRepo: InspectorRepository
) {

    private val log: Logger = LoggerFactory.getLogger(TruckInspectorController::class.java)
    private val queueSize = 2

    @GetMapping(path=["/trucks/output/notes-specId/{specId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    //@RequestMapping
    fun doTrucksOutput(@PathVariable specId: Int): VariousNotes = runBlocking<VariousNotes> {
        // uses truckSpec nos 10101, 10102, 10103

        /* from runALl - we want a full report VariousNotes
                println("runAll starting")
                here is copy/variation of runAll()

         */
        if(variousNotesRepo.findBySpecId(specId) != null){
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
        // channel.close()
        println("Source exiting")
        notes.notes.add("Source exiting")
    }

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
        // channel.close()
        println("  Filter exiting")
        notes.notes.add("  Filter exiting")
    }

    private fun CoroutineScope.output(notes:VariousNotes)
    : SendChannel<VariousNotes> = actor(capacity = queueSize) {
        println("    Output starting")
        for (x in channel) {
            notes.notes.add("    Output received $x")
            println("    Output received $x")
            val sleep: Long = (400..600).random().toLong()
            withContext(Dispatchers.IO) {
                Thread.sleep(sleep)
            }
        }
        println("    Output exiting")
        notes.notes.add("    Output exiting")
    }
}