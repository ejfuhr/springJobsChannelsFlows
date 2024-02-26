package com.example.springJobsChannelsFlows.utils

import java.io.Serializable

/**
 *
 * from https://stackoverflow.com/questions/46202147/kotlin-quadruple-quintuple-etc-for-destructuring
 * Created by nalcalag on 09/02/2019.
 *
 * Represents a quartet of values
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Quadruple exhibits value semantics
 *
 * this COULD BE Sedable.Serialzable
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @param C type of the third value.
 * @param D type of the fourth value.
 * @property first First value.
 * @property second Second value.
 * @property third Third value.
 * @property fourth Fourth value.
 */


data class QuadTuple<out A, out B, out C, out D>(

    val first: A,
    val second: B,
    val third: C,
    val fourth: D
): Serializable {
    /**
     * Returns string representation of the [Quintuple] including its [first], [second], [third], [fourth] and [fifth] values.
     */
    override fun toString(): String = "($first, $second, $third, $fourth)"
}

