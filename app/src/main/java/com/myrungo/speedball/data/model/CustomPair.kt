package com.myrungo.speedball.data.model

import java.io.Serializable

data class CustomPair<A, B>(
    var first: A,
    var second: B
) : Serializable {

    override fun toString(): String = "($first, $second)"

    infix fun <A, B> A.to(that: B): CustomPair<A, B> = CustomPair(this, that)

    fun <T> CustomPair<T, T>.toList(): List<T> = listOf(first, second)
}