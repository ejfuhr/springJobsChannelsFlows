package com.example.springJobsChannelsFlows.controller

import com.example.springJobsChannelsFlows.entity.VariousNotes
import com.example.springJobsChannelsFlows.entity.VariousNotesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/toycode")
class ToyController (
    @Autowired
    val variousNotesRepo: VariousNotesRepository
){

    private val log: Logger = LoggerFactory.getLogger(ToyController::class.java)


    @GetMapping("/launch/job")
    fun doJob1():VariousNotes= runBlocking<VariousNotes> {
        val notes:VariousNotes = VariousNotes(null, null, mutableListOf())
        val request = launch {
            repeat(3) { i -> // launch a few children jobs
                launch  {
                    delay((i + 1) * 200L) // variable delay 200ms, 400ms, 600ms
                    println("Coroutine $i is done")
                    notes.notes.add("Coroutine $i is done")
                }
            }
            println("request: I'm done and I don't explicitly join my children that are still active")
            notes.notes.add("request: I'm done and I don't explicitly join my children that are still active")
        }
        request.join() // wait for completion of the request, including all its children
        println("Now processing of the request is complete:: parental launch is complete:: ${request.isCompleted}")
        notes.notes.add("Now processing of the request is complete:: parental launch is complete:: ${request.isCompleted}")
        notes.notes.add("how many children jobs does 'request' have: ${request.children.count()}")

        return@runBlocking variousNotesRepo.save(notes)
        //notes
    }
}