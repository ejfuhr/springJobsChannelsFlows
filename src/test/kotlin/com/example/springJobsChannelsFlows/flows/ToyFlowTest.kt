package com.example.springJobsChannelsFlows.flows

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory

enum class CarType { VW, TRUCK, CADALLAC }
enum class ToyOwnerName { BOBBY, SALLY, JOE, ZALLY }
data class ToyCar(val type: CarType, val price: Double, var owner: ToyOwner?=null)
data class ToyOwner(
    val id: Int? = 101,
    val owner: ToyOwnerName? = ToyOwnerName.BOBBY,
    val address: String? = "101 main st"
)

//@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ToyFlowTest {

    private val log: Logger = LoggerFactory.getLogger(ToyFlowTest::class.java)
    private var owner1: ToyOwner? = null
    private var owner2: ToyOwner? = null
    private var owner3: ToyOwner? = null
    private var owner4: ToyOwner? = null
    private val car1 = ToyCar(CarType.CADALLAC, 2.02)
    private val car2 = ToyCar(CarType.TRUCK, 2.22)
    private val car3 = ToyCar(CarType.TRUCK, 2.44)
    private val car4 = ToyCar(CarType.VW, 2.02)
    private val car5 = ToyCar(CarType.TRUCK, 2.02)
    private val car6 = ToyCar(CarType.CADALLAC, 2.02)

    @BeforeAll
    fun doData() {
        owner1 = ToyOwner(1, ToyOwnerName.SALLY, "101 south ave")
        owner2 = ToyOwner(202, ToyOwnerName.BOBBY, "202 south ave")
        owner3 = ToyOwner(303, ToyOwnerName.JOE, "303 south ave")
        owner4 = ToyOwner(404, ToyOwnerName.ZALLY, "404 south ave")


    }

    @Test
    @Order(1)
    fun tryAsFlow() = runBlocking {
        val listOfOwners = mutableListOf(owner1, owner2, owner3, owner4).asFlow()
        listOfOwners
            .map { req -> performRequest(req) }
            .collect { response ->
                assertNotNull(response)
                println(response)
            }
    }

    private suspend fun performRequest(req: ToyOwner?): ToyCar {
        car1.owner = req!!
        delay(1000)
        return car1
    }

}