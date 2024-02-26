package com.example.springJobsChannelsFlows.utils

/**
 * Converts this quintuple into a list.
 */
fun <T> QuadTuple<T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth)

fun <D> QuadTuple<*, *, *, D>.toMutableList():MutableList<*> = mutableListOf(first, second, third, fourth)
