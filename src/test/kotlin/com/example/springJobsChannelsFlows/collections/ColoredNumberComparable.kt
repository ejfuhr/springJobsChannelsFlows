package com.example.springJobsChannelsFlows.collections


class ColoredNumberComparable(var value: Int, var color: String) : Comparable<ColoredNumberComparable?> {

    override fun compareTo(other: ColoredNumberComparable?): Int {
        // (both numbers are red) or (both numbers are not red)
        return if (color == "red" && other!!.color == "red" || color != "red" && other!!.color != "red") {
            Integer.compare(value, other.value)
        } else if (color == "red") {
            -1
        } else {
            1
        }
    }

}
