package com.example.springJobsChannelsFlows.timedate

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.parse
import java.time.format.DateTimeFormatter

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TimeDateTest {

    private val log: Logger = LoggerFactory.getLogger(TimeDateTest::class.java)

    @Test
    @Order(1)
    fun `do time test 1`(){
        val localDate = LocalDate.parse("2022-01-06")
        assertThat(localDate).isEqualTo("2022-01-06")
    }

    @Test
    @Order(2)
    fun givenString_whenCustomFormat_thenLocalDateCreated() {
        val localDate = LocalDate.parse("01-06-2022", DateTimeFormatter.ofPattern("MM-dd-yyyy"))
        assertThat(localDate).isEqualTo("2022-01-06")
    }

    @Test
    @Order(3)
    fun givenString_whenCustomFormat_thenLocalDateTimeCreated() {
        val text = "2022-01-06 20:30:45"
        val moretime = "2022"
        val moretime2 = moretime.plus("-01").plus("-06").plus(" 20").plus(":30").plus(":45")
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val localDateTime = LocalDateTime.parse(text, pattern)

        val localDateTime2 = parse(moretime2, pattern)

        assertThat(localDateTime).isEqualTo("2022-01-06T20:30:45")
        log.debug("more time2 {}", moretime2)
        assertThat(localDateTime2).isEqualTo("2022-01-06T20:30:45")
    }
}