package com.example.springJobsChannelsFlows.channels

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

class FlowCollectorsTest {

    @Test
    @Order(1)
    fun `test sharedFlow`() = runBlocking {
        sharedFlow()
    }

    @Test
    @Order(3)
    fun `do this for notnull`() = runBlocking {
        assertNotNull("a")
    }

    @Test
    @Order(2)
    //@Disabled
    fun `test from demystifying ChannelsFlows`() = runBlocking {

            assertNotNull("l")
            doFlow()
                .map {
                    assertThat(it).isEqualTo("zz")
                    println("it $it")
                }
                .collect()
        }
}


private suspend fun doFlow(): Flow<String> = flow<String> {
    coroutineScope {
        val output = mutableListOf("hello", "world", "kool").asFlow()
            .onCompletion {
                println("this is upstream list as flow")
            }
            .shareIn(this, SharingStarted.Lazily)

        val shareJob = output
            .onEach {
                println("Flow collector received or add this to new Collection '$it'")
            }
            .onCompletion { "completeion in shareJob whats here $it" }
            .launchIn(this)

        launch {
            while (shareJob.isActive) {
                println("Flow collector (shareJob) is waiting for more values...")
                delay(50)
            }

            //assertThat(output.toList().size).isGreaterThan(2)

        }
        delay(150)
        shareJob.cancel("done with flow stuff")

    }
}

private suspend fun sharedFlow() = coroutineScope {

    val wordsList = mutableListOf("Hello World", "wow").asFlow()
    val noList = flowOf(wordsList)
    val sharedFlow = wordsList//flowOf("Hello World", "wow")//flowOf("Hello, World!")
        .onCompletion { println("Upstream flow has no more values") }
        .shareIn(this, SharingStarted.Lazily) //  try removing this line!

    val collector = sharedFlow
        .onEach { println("Flow collector received '$it'") }
        .onCompletion { println("Flow collection stopped, error was $it") }
        .launchIn(this)

    launch {
        while (collector.isActive) {
            println("Flow collector is waiting for more values...")
            delay(50)
        }
    }
    delay(250)
    collector.cancel("Giving up waiting")

}

