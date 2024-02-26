package com.example.springJobsChannelsFlows.channels

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

data class Campsite(val id: Int, val name: String, val highway: String?, var campers: List<Camper>)
data class Camper(val id: Int, val name: String, val number:String?)

var siteChannnel = Channel<Campsite>()
var campChannel = Channel<Camper>()



class FoolAroundCampsiteTest {

    @Test
    fun channelsAndJobTest() = runBlocking {
        val channel = Channel<Int>()
        val job = launch {
            var c1 = Camper(1, "chuckie", "101")
            //listOf(c1)
            var site1 = Campsite(101, "abq1", "64", listOf(c1))
            siteChannnel.send(site1)
            // this might be heavy CPU-consuming computation or async logic, we'll just send five squares
            for (x in 1..5) {
                channel.send(x * x)
            }
        }

/*        repeat(2){
            siteChannnel.receive()
        }*/

        var siteR = siteChannnel.receive()
        println("siteChannel.receive(): " + siteR)
        // here we print five received integers:
        assertThat(siteR).isNotNull
        assertTrue(siteChannnel.isEmpty)
        //assertNotNull(siteChannnel.receive())

        repeat(5) { println(channel.receive()) }
        println("job children ${job.children.count()} job complete? ${job.isCompleted} job active ${job.isActive}")

        job.cancelAndJoin()
        println("job children ${job.children.count()} job complete? ${job.isCompleted} job active ${job.isActive}")
        println("Done!")
    }

}
