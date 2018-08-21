package frc.team5190.lib.wrappers.hid

import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.XboxController

// Builder Helpers
fun xboxController(port: Int, block: FalconHIDBuilder<XboxController>.() -> Unit) = controller(XboxController(port), block)

fun FalconHIDBuilder<XboxController>.button(button: XboxButton, block: FalconHIDButtonBuilder.() -> Unit = {}) = button(button.value, block)
fun FalconHIDBuilder<XboxController>.triggerAxisButton(hand: GenericHID.Hand, threshold: Double = HIDButton.DEFAULT_THRESHOLD, block: FalconHIDButtonBuilder.() -> Unit = {})
        = axisButton(yTriggerAxisToRawAxis(hand), threshold, block)

// Source Helpers
fun FalconHID<XboxController>.getY(hand: GenericHID.Hand) = getRawAxis(yAxisToRawAxis(hand))
fun FalconHID<XboxController>.getX(hand: GenericHID.Hand) = getRawAxis(xAxisToRawAxis(hand))
fun FalconHID<XboxController>.getRawButton(button: XboxButton) = getRawButton(button.value)

private fun yAxisToRawAxis(hand: GenericHID.Hand) = if(hand == GenericHID.Hand.kLeft) 1 else 5
private fun xAxisToRawAxis(hand: GenericHID.Hand) = if(hand == GenericHID.Hand.kLeft) 0 else 4
private fun yTriggerAxisToRawAxis(hand: GenericHID.Hand) = if(hand == GenericHID.Hand.kLeft) 2 else 3

val kBumperLeft = XboxButton(5)
val kBumperRight = XboxButton(6)
val kStickLeft = XboxButton(9)
val kStickRight = XboxButton(10)
val kA = XboxButton(1)
val kB = XboxButton(2)
val kX = XboxButton(3)
val kY = XboxButton(4)
val kBack = XboxButton(7)
val kStart = XboxButton(8)

class XboxButton internal constructor(val value: Int)