package com.example.springJobsChannelsFlows.services

import com.example.springJobsChannelsFlows.entity.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class InspectionServicesTest(
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

    private val log: Logger = LoggerFactory.getLogger(InspectionServicesTest::class.java)
    private val reportSpecId = 90909
    private var report: InspectionReport? = null

    @BeforeAll
    fun doData() = runBlocking{
        dailyRepo.deleteAll()
        reportRepo.deleteAll()
        report = inspectionReport(reportSpecId)
    }

    private suspend fun inspectionReport(specId:Int): InspectionReport {
        val report = reportRepo.save(
            InspectionReport(
                null, specId, LocalDate.now(), mutableListOf<Truck>(),
                mutableListOf<Inspector>(),
                mutableListOf<Truck>(),
                mutableListOf<String>("just a note", "watch out for the red shoes")
            )
        )
        return report
    }

    @Test
    @Order(1)
    fun `test inspectio to dailyRepo`() = runBlocking{

        val report = reportRepo.findBySpecId(reportSpecId)

        val daily = DailyInspection(null, 10101, mutableListOf(report))
        val daily2 = dailyRepo.save(daily)
        assertNotNull(daily2)
        assertThat(daily2.inspectionReports.size).isEqualTo(1)
        assertThat(daily2.specId).isEqualTo(10101)

        for(x in daily2.inspectionReports) {
            for (y in x.notes) {
                log.info("daily reports notes {}", y)
            }
        }
    }

    @Test
    @Order(2)
    fun testFoo()  = runTest{
        launch {
            delay(1_000)
            println("1. $currentTime")
        }
        val deferred = async {
            delay(3_000)
            println("3. $currentTime")
        }
        assertTrue(2>1)
        deferred.await()
    }


    @Test
    @Order(3)
    fun testWithMultipleDispatchers() = runTest {
        val scheduler = testScheduler // the scheduler used for this test
        val dispatcher1 = StandardTestDispatcher(scheduler, name = "IO dispatcher")
        val dispatcher2 = StandardTestDispatcher(scheduler, name = "Background dispatcher")
        launch(dispatcher1) {
            delay(1_000)
            println("1. $currentTime") // 1000
            delay(200)
            println("2. $currentTime") // 1200
            delay(2_000)
            println("4. $currentTime") // 3200
        }
        val deferred = async(dispatcher2) {
            delay(3_000)
            println("3. $currentTime") // 3000
            delay(500)
            println("5. $currentTime") // 3500
        }
        deferred.await()
    }



}