package frc.team5190.lib.wrappers.hid

import edu.wpi.first.wpilibj.GenericHID
import frc.team5190.lib.commands.Command
import frc.team5190.lib.utils.BooleanSource
import frc.team5190.lib.utils.DoubleSource
import frc.team5190.lib.utils.withThreshold

fun <T : GenericHID> controller(genericHID: T, block: FalconHIDBuilder<T>.() -> Unit): FalconHID<T> {
    val builder = FalconHIDBuilder(genericHID)
    block(builder)
    return builder.build()
}

class FalconHIDBuilder<T : GenericHID>(private val genericHID: T) {
    private val controlBuilders = mutableListOf<FalconHIDControlBuilder>()

    fun button(buttonId: Int, block: FalconHIDButtonBuilder.() -> Unit = {}) = button(HIDButtonSource(genericHID, buttonId), block = block)
    fun axisButton(axisId: Int, threshold: Double = HIDButton.DEFAULT_THRESHOLD, block: FalconHIDButtonBuilder.() -> Unit = {}) = button(HIDAxisSource(genericHID, axisId), threshold, block)
    fun pov(angle: Int, block: FalconHIDButtonBuilder.() -> Unit = {}) = pov(0, angle, block)
    fun pov(pov: Int, angle: Int, block: FalconHIDButtonBuilder.() -> Unit = {}) = button(HIDPOVSource(genericHID, pov, angle), block = block)

    fun button(source: HIDSource, threshold: Double = HIDButton.DEFAULT_THRESHOLD, block: FalconHIDButtonBuilder.() -> Unit = {}): FalconHIDButtonBuilder {
        val builder = FalconHIDButtonBuilder(source, threshold)
        controlBuilders.add(builder)
        block(builder)
        return builder
    }

    fun build(): FalconHID<T> {
        val controls = controlBuilders.map { it.build() }
        return FalconHID(genericHID, controls)
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

    fun change(command: Command) {
        changeOn(command)
        changeOff { command.stop() }
    }

    fun changeOn(command: Command) = changeOn { command.start() }
    fun changeOff(command: Command) = changeOff { command.start() }

    fun whileOff(block: HIDControlListener) = also { whileOff.add(block) }
    fun whileOn(block: HIDControlListener) = also { whileOn.add(block) }
    fun changeOn(block: HIDControlListener) = also { changeOn.add(block) }
    fun changeOff(block: HIDControlListener) = also { changeOff.add(block) }

    override fun build() = HIDButton(source, threshold, whileOff, whileOn, changeOn, changeOff)
}

class FalconHID<T : GenericHID>(private val genericHID: T,
                                private val controls: List<HIDControl>) {

    fun getRawAxis(axisId: Int): DoubleSource = HIDAxisSource(genericHID, axisId)
    fun getRawButton(buttonId: Int): BooleanSource = HIDButtonSource(genericHID, buttonId).booleanSource

    suspend fun update() = controls.forEach { it.update() }

}