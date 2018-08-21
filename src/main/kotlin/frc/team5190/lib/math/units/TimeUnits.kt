/*
 * FRC Team 5190
 * Green Hope Falcons
 */

@file:Suppress("PropertyName")

package frc.team5190.lib.math.units

interface Time {
    val SEC: Double
    val MS: Int
}

class Seconds(val value: Double) : Time {
    override val SEC
        get() = value
    override val MS
        get() = (value * 1000).toInt()
}

class Milliseconds(val value: Int) : Time {
    override val MS
        get() = value
    override val SEC
        get() = value / 1000.0
}