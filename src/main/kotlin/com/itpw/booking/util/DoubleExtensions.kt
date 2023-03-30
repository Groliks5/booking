package com.itpw.booking.util

import kotlin.math.abs

fun Double.equalsDelta(other: Double) = abs(this / other - 1) < 0.000001