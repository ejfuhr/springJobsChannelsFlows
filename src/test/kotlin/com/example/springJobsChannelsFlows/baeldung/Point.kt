package com.example.springJobsChannelsFlows.baeldung

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test

/**
 * jst a note and "translated" from
 * https://vived.substack.com/p/loom-strikes-again-what-improvements
 */

@JvmRecord
internal data class Point(val x: Int, val y: Int, val z: Int) {
    fun withX(newX: Int): Point {
        return Point(newX, y, z)
    }

    fun withY(newY: Int): Point {
        return Point(x, newY, z)
    }

    fun withZ(newZ: Int): Point {
        return Point(x, y, newZ)
    }
}

class PointTest{

    @Test
    @Order(1)
    fun `test Point no 1`(){
        val pt = Point(2,3,4)
        val ptx = pt.withX(4)
        assertThat(pt.x).isEqualTo(2)
        assertThat(ptx.x).isEqualTo(4)
    }
}