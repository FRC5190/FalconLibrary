/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers.hid

import edu.wpi.first.wpilibj.XboxController

typealias FalconXboxController = FalconHID<XboxController>
typealias FalconXboxBuilder = FalconHIDBuilder<XboxController>

// Builder Helpers
fun xboxController(port: Int, block: FalconXboxBuilder.() -> Unit): FalconXboxController =
    XboxController(port).mapControls(block)

fun FalconXboxBuilder.button(
    button: XboxButton,
    block: FalconHIDButtonBuilder.() -> Unit = {},
) = button(button.value, block)

fun FalconXboxBuilder.triggerAxisButton(
    hand: Hand,
    threshold: Double = HIDButton.DEFAULT_THRESHOLD,
    block: FalconHIDButtonBuilder.() -> Unit = {},
) = axisButton(yTriggerAxisToRawAxis(hand), threshold, block)

// Source Helpers
fun FalconXboxController.getY(hand: Hand) = getRawAxis(
    yAxisToRawAxis(
        hand,
    ),
)

fun FalconXboxController.getX(hand: Hand) = getRawAxis(
    xAxisToRawAxis(
        hand,
    ),
)

fun FalconXboxController.getRawButton(button: XboxButton) = getRawButton(button.value)

private fun yAxisToRawAxis(hand: Hand) = if (hand == Hand.Left) 1 else 5
private fun xAxisToRawAxis(hand: Hand) = if (hand == Hand.Left) 0 else 4
private fun yTriggerAxisToRawAxis(hand: Hand) = if (hand == Hand.Left) 2 else 3

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

data class XboxButton internal constructor(val value: Int)
