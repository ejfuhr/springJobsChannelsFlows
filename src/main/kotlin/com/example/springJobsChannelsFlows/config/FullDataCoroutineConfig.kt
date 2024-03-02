package com.example.springJobsChannelsFlows.config

import com.example.springJobsChannelsFlows.entity.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.random.Random
import kotlin.random.nextInt

@Configuration
@Profile("data")
class FullDataCoroutineConfig {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun trucksInspectionsInit(
        @Autowired
        truckRepo: TruckRepository,
        @Autowired
        inspectorRepo: InspectorRepository,
        @Autowired
        variousNotesRepo: VariousNotesRepository,
        @Autowired
        reportRepo: InspectionReportRepository,
        @Autowired
        dailyRepo: DailyAllReportRepository,
    ) = ApplicationRunner {
        log.debug("In appRunner")

        runBlocking {
            truckRepo.deleteAll()
            inspectorRepo.deleteAll()
            reportRepo.deleteAll()
            dailyRepo.deleteAll()
        }

        val no = 6
        runBlocking {
            for (n in 1..no) {
                var truck1 = Truck(null, 10100 + n, "georgia " + n, "bob ".plus(Random.nextInt(until = 101)))
                truckRepo.save(truck1)
            }
        }
        val inspect1 = Inspector(null, 10101, "spector ".plus(doNameNo()), InspectorDivision.MGMT)
        val inspect2 = Inspector(null, 10102, "spector ".plus(doNameNo()), InspectorDivision.CRIME)
        val inspect3 = Inspector(null, 10103, "spector ".plus(doNameNo()), InspectorDivision.GOODS)
        val inspect4 = Inspector(null, 10104, "spector ".plus(doNameNo()), InspectorDivision.BEGINNER)
        val inspect5 = Inspector(null, 10105, "corn  ".plus(doNameNo()), InspectorDivision.AGRICULTURE)
        val inspect6 = Inspector(null, 10106, "apples ".plus(doNameNo()), InspectorDivision.AGRICULTURE)

        val mylist = mutableListOf(inspect1, inspect2, inspect3, inspect4, inspect5, inspect6)
        runBlocking {
            for (spect in mylist) {
                inspectorRepo.save(spect)
            }
        }

    }

    fun doNameNo():Int = Random.nextInt(101)
}