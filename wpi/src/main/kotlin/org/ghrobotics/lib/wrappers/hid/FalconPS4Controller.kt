/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers.hid

import edu.wpi.first.wpilibj.Joystick

typealias FalconPS4Controller = FalconHID<Joystick>
typealias FalconPS4Builder = FalconHIDBuilder<Joystick>

// Builder helpers
fun ps4Controller(port: Int, block: FalconPS4Builder.() -> Unit): FalconPS4Controller =
    Joystick(port).mapControls(block)

fun FalconPS4Builder.button(
    button: PS4Button,
    block: FalconHIDButtonBuilder.() -> Unit = {},
) = button(button.value, block)

fun FalconPS4Builder.triggerAxisButton(
    hand: Hand,
    threshold: Double = HIDButton.DEFAULT_THRESHOLD,
    block: FalconHIDButtonBuilder.() -> Unit = {},
) = axisButton(yTriggerAxisToRawAxis(hand), threshold, block)

// Source helpers
fun FalconPS4Controller.getY(hand: Hand) = getRawAxis(yAxisToRawAxis(hand))

fun FalconPS4Controller.getX(hand: Hand) = getRawAxis(xAxisToRawAxis(hand))
fun FalconPS4Controller.getRawButton(button: XboxButton) = getRawButton(button.value)

private fun yAxisToRawAxis(hand: Hand) = if (hand == Hand.Left) 1 else 5
private fun xAxisToRawAxis(hand: Hand) = if (hand == Hand.Left) 0 else 2
private fun yTriggerAxisToRawAxis(hand: Hand) = if (hand == Hand.Left) 2 else 3

enum class PS4Button(val value: Int) {
    Square(1),
    X(2),
    Circle(3),
    Triangle(4),
    BumperLeft(5),
    BumperRight(6),
    TriggerLeft(7),
    TriggerRight(8),
    Share(9),
    Options(10),
    StickLeft(11),
    StickRight(12),
    Playstation(13),
    Touchpad(14),
}
