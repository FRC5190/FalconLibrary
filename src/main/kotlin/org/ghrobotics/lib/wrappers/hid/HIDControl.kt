package org.ghrobotics.lib.wrappers.hid

class HIDButton(
    private val source: HIDSource,
    private val threshold: Double,
    private val whileOff: List<HIDControlListener>,
    private val whileOn: List<HIDControlListener>,
    private val changeOn: List<HIDControlListener>,
    private val changeOff: List<HIDControlListener>
) : HIDControl {

    companion object {
        const val DEFAULT_THRESHOLD = 0.5
    }

    private var lastValue = source() >= threshold

    override suspend fun update() {
        val newValue = source() >= threshold
        when {
            // Value has changed
            lastValue != newValue -> when {
                newValue -> changeOn
                else -> changeOff
            }
            // Value stayed the same
            else -> when {
                newValue -> whileOn
                else -> whileOff
            }
        }.forEach { it() }
        lastValue = newValue
    }
}

typealias HIDControlListener = suspend () -> Unit

interface HIDControl {
    suspend fun update()
}