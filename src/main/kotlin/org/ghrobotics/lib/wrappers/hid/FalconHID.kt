package org.ghrobotics.lib.wrappers.hid

import edu.wpi.first.wpilibj.GenericHID
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.DoubleSource

fun <T : GenericHID> T.mapControls(
    block: FalconHIDBuilder<T>.() -> Unit
) = FalconHIDBuilder(this).also(block).build()

class FalconHIDBuilder<T : GenericHID>(private val genericHID: T) {
    private val controlBuilders = mutableListOf<FalconHIDControlBuilder>()
    private val stateControlBuilders = mutableMapOf<BooleanSource, FalconHIDBuilder<T>>()

    fun button(
        buttonId: Int,
        block: FalconHIDButtonBuilder.() -> Unit = {}
    ) = button(HIDButtonSource(genericHID, buttonId), block = block)

    fun axisButton(
        axisId: Int,
        threshold: Double = HIDButton.DEFAULT_THRESHOLD,
        block: FalconHIDButtonBuilder.() -> Unit = {}
    ) = button(HIDAxisSource(genericHID, axisId), threshold, block)

    fun pov(angle: Int, block: FalconHIDButtonBuilder.() -> Unit = {}) = pov(0, angle, block)
    fun pov(pov: Int, angle: Int, block: FalconHIDButtonBuilder.() -> Unit = {}) =
        button(HIDPOVSource(genericHID, pov, angle), block = block)

    fun state(state: BooleanSource, block: FalconHIDBuilder<T>.() -> Unit) =
        stateControlBuilders.compute(state) { _, _ -> FalconHIDBuilder(genericHID).also(block) }

    fun button(
        source: HIDSource,
        threshold: Double = HIDButton.DEFAULT_THRESHOLD,
        block: FalconHIDButtonBuilder.() -> Unit = {}
    ): FalconHIDButtonBuilder {
        val builder = FalconHIDButtonBuilder(source, threshold)
        controlBuilders.add(builder)
        block(builder)
        return builder
    }

    fun build(): FalconHID<T> {
        val controls = controlBuilders.map { it.build() }
        return FalconHID(genericHID, controls, stateControlBuilders.mapValues { it.value.build() })
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

    fun change(command: FalconCommand) {
        changeOn(command)
        changeOff { command.stop() }
    }

    fun changeOn(command: FalconCommand) = changeOn { command.start() }
    fun changeOff(command: FalconCommand) = changeOff { command.start() }

    fun whileOff(block: HIDControlListener) = also { whileOff.add(block) }
    fun whileOn(block: HIDControlListener) = also { whileOn.add(block) }
    fun changeOn(block: HIDControlListener) = also { changeOn.add(block) }
    fun changeOff(block: HIDControlListener) = also { changeOff.add(block) }

    override fun build() = HIDButton(source, threshold, whileOff, whileOn, changeOn, changeOff)
}

class FalconHID<T : GenericHID>(
    private val genericHID: T,
    private val controls: List<HIDControl>,
    private val stateControls: Map<BooleanSource, FalconHID<T>>
) {

    fun getRawAxis(axisId: Int): DoubleSource = HIDAxisSource(genericHID, axisId)
    fun getRawButton(buttonId: Int): BooleanSource = HIDButtonSource(genericHID, buttonId).booleanSource

    fun update() {
        controls.forEach { it.update() }
        for ((state, controller) in stateControls) {
            if (state()) controller.update()
        }
    }
}