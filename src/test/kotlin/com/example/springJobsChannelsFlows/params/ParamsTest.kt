package com.example.springJobsChannelsFlows.params

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

fun multiply(a: Int, b: Int): Int = a * b
fun addMe(a:Int, b: Int): Int = a+b
class ParamsTest {

    @Order(1)
    @ParameterizedTest
    @MethodSource("multiplicationArguments")
    fun `given two numbers, should return their product`(a: Int, b: Int, expectedProduct: Int) {
        // When
        val result = multiply(a, b)

        // Then
        assertEquals(expectedProduct, result)
    }

    companion object {
        @JvmStatic
        fun multiplicationArguments(): Stream<Arguments> =
            Stream.of(
                Arguments.of(2, 3, 6),
                Arguments.of(5, 4, 20),
                Arguments.of(0, 8, 0),
                Arguments.of(-3, 2, -6),
                Arguments.of(-3, -3, 9)
            )
        @JvmStatic
        fun moreArgs(): Stream<Arguments> =
            Stream.of(
                Arguments.of(20, 2, 22),
                Arguments.of(40, 4, 44)
            )
    }
    @Order(2)
    @ParameterizedTest
    @MethodSource("moreArgs")
    fun `given two numbers, should return their sum`(a: Int, b: Int, expectedSum: Int) {

        val result = addMe(a, b)
        assertEquals(expectedSum, result)

    }


}