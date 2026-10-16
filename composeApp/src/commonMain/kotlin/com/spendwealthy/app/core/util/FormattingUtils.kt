package com.spendwealthy.app.core.util

import kotlin.math.roundToInt
import kotlin.math.pow

fun Double.format(decimals: Int): String {
    val factor = 10.0.pow(decimals.toDouble())
    val rounded = (this * factor).roundToInt() / factor
    return rounded.toString()
}

fun Long.toKFormat(): String {
    return "${this / 1000}k"
}
