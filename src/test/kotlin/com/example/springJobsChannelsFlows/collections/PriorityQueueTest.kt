package com.example.springJobsChannelsFlows.collections

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.*


class PriorityQueueComparatorUnitTest {
    @Test
    fun givenIntegerQueue_defaultComparator_followsNaturalOrdering() {
        val integerQueue: PriorityQueue<Int> = PriorityQueue()
        val integerQueueWithComparator: PriorityQueue<Int> = PriorityQueue { c1: Int?, c2: Int? ->
            Integer.compare(
                c1!!, c2!!
            )
        }
        integerQueueWithComparator.add(3)
        integerQueue.add(3)
        integerQueueWithComparator.add(2)
        integerQueue.add(2)
        integerQueueWithComparator.add(1)
        integerQueue.add(1)
        assertThat(integerQueue.poll()).isEqualTo(1).isEqualTo(integerQueueWithComparator.poll())
        assertThat(integerQueue.poll()).isEqualTo(2).isEqualTo(integerQueueWithComparator.poll())
        assertThat(integerQueue.poll()).isEqualTo(3).isEqualTo(integerQueueWithComparator.poll())
    }

    @Test
    fun givenIntegerQueue_reverseOrderComparator_followsInverseNaturalOrdering() {
        val reversedQueue: PriorityQueue<Int> = PriorityQueue(Collections.reverseOrder())
        reversedQueue.add(1)
        reversedQueue.add(2)
        reversedQueue.add(3)
        assertThat(reversedQueue.poll()).isEqualTo(3)
        assertThat(reversedQueue.poll()).isEqualTo(2)
        assertThat(reversedQueue.poll()).isEqualTo(1)
    }

    @Test
    fun givenNotComparableQueue_classCastException() {
        assertThatThrownBy {
            val queue: PriorityQueue<ColoredNumber> = PriorityQueue()
            queue.add(ColoredNumber(3, "red"))
            queue.add(ColoredNumber(2, "blue"))
        }.isInstanceOf(ClassCastException::class.java)
    }

    @Test
    fun givenCustomOrderingQueue_orderIsCorrect() {
        val queue: PriorityQueue<ColoredNumberComparable> = PriorityQueue()
        queue.add(ColoredNumberComparable(10, "red"))
        queue.add(ColoredNumberComparable(20, "red"))
        queue.add(ColoredNumberComparable(1, "blue"))
        queue.add(ColoredNumberComparable(2, "blue"))
        val first: ColoredNumberComparable = queue.poll()
        assertThat(first.color).isEqualTo("red")
        assertThat(first.value).isEqualTo(10)
        queue.poll()
        val third: ColoredNumberComparable = queue.poll()
        assertThat(third.color).isEqualTo("blue")
        assertThat(third.value).isEqualTo(1)
    }
}