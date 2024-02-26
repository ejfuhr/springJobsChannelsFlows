package com.example.springJobsChannelsFlows.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

import io.mockk.core.ValueClassSupport.boxedValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order

@SpringBootTest
//@EnableMongoRepositories
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class NTuplePlusTest {

    private val log: Logger = LoggerFactory.getLogger(NTuplePlusTest::class.java)

    @Test
    @Order(1)
    fun `test ntuples one`() {
        val nTuple4 = 1 then 2 then "foo" then "bar"
        val boxed = nTuple4.boxedValue
        assertNotNull(boxed)
        boxed.then(22)
        assertFalse(nTuple4.t2 == 22)

        log.info("boxed, the is ${nTuple4.t2} or ${boxed.then(22)}")
    }

    @Test
    @Order(2)
    fun `test NTuple Stuff with toMono`() {
        val nTuple4 = 1 then 2 then "foo" then "bar"
        log.debug("nTuple4-1 is ${nTuple4.t1} and nTuple4-4 is ${nTuple4.t4}")
        assertEquals("bar", nTuple4.t4)
        val nTuple4mono: Mono<NTuple4<Int, Int, String, String>> = nTuple4.toMono()

        nTuple4mono
            .test()
            .assertNext { n ->
                assertTrue(n.t1 == 1)
                assertTrue(n.t2 == 2)
            }
            .verifyComplete()

    }

    @Test
    @Order(3)
    fun `test NTuples to change to Flow`() {
        val nTuple4 = 1 then 2 then "foo" then "bar"
        //runBlocking {
        //flow<NTuple4<Int, Int, String, String>> {
        val myInt = nTuple4.t1
        assertThat(myInt).isEqualTo(1)

        //now add a Tiny data class using then..
        val tony1 = Tiny("one", "tony")
        val tuple5: NTuple5<Int, Int, String, String, Tiny> = nTuple4.then(tony1)
        assertThat(tuple5.t5.myName).isEqualTo("tony")
        log.debug("T5 to string {}", nTuple4.toString())
        log.debug("here is t5 or nTuple.then {}", tuple5.toString())
        log.debug("here is Tiny data file after then() id:{} myName:{}", tuple5.t5.id, tuple5.t5.myName)
        assertThat(tuple5.t5.myName).isEqualTo("tony")

        //we built an nTuple4 and nTuple5
        val flowTuple = simpleFlowFromNTuple(101, 102, "what", "not now")
        runBlocking {
            launch {
                flowTuple
                    .collect { n ->
                        println(n.t1)
                        assertNotNull(n.t3)
                        assertThat(n.t3).isEqualTo("what")
                    }
            }

            flowTuple.collect { value ->
                println("in last collect " + value)
            }
        }

    }

    fun simpleFlowFromNTuple(no1: Int, no2: Int, str1: String, str2: String):
            Flow<NTuple4<Int, Int, String, String>> = flow {
        val myTuple4: NTuple4<Int, Int, String, String> = no1 then no2 then str1 then str2
        emit(myTuple4)
    }
}

data class Tiny(
    val id: String? = "null to start",
    val myName: String? = null
)