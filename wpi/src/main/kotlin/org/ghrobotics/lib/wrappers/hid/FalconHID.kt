/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers.hid

import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj2.command.Command
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.DoubleSource

fun <T : GenericHID> T.mapControls(
    block: FalconHIDBuilder<T>.() -> Unit,
) = FalconHIDBuilder(this).also(block).build()

class FalconHIDBuilder<T : GenericHID>(val genericHID: T) {
    private val controlBuilders = mutableListOf<FalconHIDControlBuilder>()
    private val stateControlBuilders = mutableMapOf<BooleanSource, FalconHIDBuilder<T>>()

    fun button(
        buttonId: Int,
        block: FalconHIDButtonBuilder.() -> Unit = {},
    ) = button(HIDButtonSource(genericHID, buttonId), block = block)

    fun axisButton(
        axisId: Int,
        threshold: Double = HIDButton.DEFAULT_THRESHOLD,
        block: FalconHIDButtonBuilder.() -> Unit = {},
    ) = button(HIDAxisSource(genericHID, axisId), threshold, block)

    fun lessThanAxisButton(
        axisId: Int,
        threshold: Double = HIDButton.DEFAULT_THRESHOLD,
        block: FalconHIDButtonBuilder.() -> Unit = {},
    ) = boundedAxisButton(axisId, threshold, -1.0..0.0, block)

    fun greaterThanAxisButton(
        axisId: Int,
        threshold: Double = HIDButton.DEFAULT_THRESHOLD,
        block: FalconHIDButtonBuilder.() -> Unit = {},
    ) = boundedAxisButton(axisId, threshold, 0.0..1.0, block)

    fun boundedAxisButton(
        axisId: Int,
        threshold: Double = HIDButton.DEFAULT_THRESHOLD,
        range: ClosedFloatingPointRange<Double>,
        block: FalconHIDButtonBuilder.() -> Unit = {},
    ) = button(BoundedHIDAxisSource(genericHID, axisId, range), threshold, block)

    fun pov(angle: Int, block: FalconHIDButtonBuilder.() -> Unit = {}) = pov(0, angle, block)
    fun pov(pov: Int, angle: Int, block: FalconHIDButtonBuilder.() -> Unit = {}) =
        button(HIDPOVSource(genericHID, pov, angle), block = block)

    fun state(state: BooleanSource, block: FalconHIDBuilder<T>.() -> Unit) =
        stateControlBuilders.compute(state) { _, _ -> FalconHIDBuilder(genericHID).also(block) }

    fun button(
        source: HIDSource,
        threshold: Double = HIDButton.DEFAULT_THRESHOLD,
        block: FalconHIDButtonBuilder.() -> Unit = {},
    ): FalconHIDButtonBuilder {
        val builder = FalconHIDButtonBuilder(source, threshold)
        controlBuilders.add(builder)
        block(builder)
        return builder
    }

    fun build(): FalconHID<T> {
        val controls = controlBuilders.map { it.build() }
        return FalconHID(
            genericHID,
            controls,
            stateControlBuilders.mapValues { it.value.build() },
        )
    }
}

abstract class FalconHIDControlBuilder(val source: HIDSource) {
    abstract fun build(): HIDControl
}

class FalconHIDButtonBuilder(source: HIDSource, private val threshold: Double) : FalconHIDControlBuilder(source) {
    private val whileOff = mutableListOf<HIDControlListener>()
    private val whileOn = mutableListOf<HIDControlListener>()
    private val changeOn = mutableListOf<HIDControlListener>()
    private val changeOff = mutableListOf<HIDControlListener>()

    fun change(command: Command): FalconHIDButtonBuilder {
        changeOn(command)
        changeOff { command.cancel() }
        return this
    }

    fun changeOn(command: Command) = changeOn { command.schedule() }
    fun changeOff(command: Command) = changeOff { command.cancel() }

    fun whileOff(block: HIDControlListener) = also { whileOff.add(block) }
    fun whileOn(block: HIDControlListener) = also { whileOn.add(block) }
    fun changeOn(block: HIDControlListener) = also { changeOn.add(block) }
    fun changeOff(block: HIDControlListener) = also { changeOff.add(block) }

    override fun build() =
        HIDButton(source, threshold, whileOff, whileOn, changeOn, changeOff)
}

class FalconHID<T : GenericHID>(
    private val genericHID: T,
    private val controls: List<HIDControl>,
    private val stateControls: Map<BooleanSource, FalconHID<T>>,
) {

    fun getRawAxis(axisId: Int): DoubleSource = HIDAxisSource(genericHID, axisId)
    fun getRawButton(buttonId: Int): BooleanSource = HIDButtonSource(
        genericHID,
        buttonId,
    ).booleanSource

    fun update() {
        controls.forEach { it.update() }
        for ((state, controller) in stateControls) {
            if (state()) controller.update()
        }
    }
}
