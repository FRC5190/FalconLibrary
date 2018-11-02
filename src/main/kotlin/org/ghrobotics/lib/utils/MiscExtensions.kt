/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.utils

import com.ctre.phoenix.CANifier
import java.awt.Color

fun CANifier.setLEDOutput(color: Color) = setLEDOutput(color.red, color.green, color.blue)

fun CANifier.setLEDOutput(r: Int, g: Int, b: Int) {
    setLEDOutput(r * (1.0 / 255.0), CANifier.LEDChannel.LEDChannelB)
    setLEDOutput(g * (1.0 / 255.0), CANifier.LEDChannel.LEDChannelA)
    setLEDOutput(b * (1.0 / 255.0), CANifier.LEDChannel.LEDChannelC)
}

val Pair<Double, Double>.l get() = this.first
val Pair<Double, Double>.r get() = this.second
