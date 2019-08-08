package org.ghrobotics.lib.localization

import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.mathematics.units.unitlessValue
import org.ghrobotics.lib.types.Interpolatable
import org.ghrobotics.lib.utils.Source
import java.util.*

class TimeInterpolatableBuffer<T : Interpolatable<T>>(
    private val historySpan: SIUnit<Second> = 1.0.second,
    private val timeSource: Source<SIUnit<Second>> = { Timer.getFPGATimestamp().second }
) {

    private val bufferMap = TreeMap<SIUnit<Second>, T>()

    operator fun set(time: SIUnit<Second>, value: T): T? {
        cleanUp()
        return bufferMap.put(time, value)
    }

    private fun cleanUp() {
        val currentTime = timeSource()

        while (bufferMap.isNotEmpty()) {
            val entry = bufferMap.lastEntry()
            if (currentTime - entry.key >= historySpan) {
                bufferMap.remove(entry.key)
            } else {
                return
            }
        }
    }

    fun clear() {
        bufferMap.clear()
    }

    operator fun get(time: SIUnit<Second>): T? {
        if (bufferMap.isEmpty()) return null

        bufferMap[time]?.let { return it }

        val topBound = bufferMap.ceilingEntry(time)
        val bottomBound = bufferMap.floorEntry(time)

        return when {
            topBound == null -> bottomBound.value
            bottomBound == null -> topBound.value
            else -> bottomBound.value.interpolate(
                topBound.value,
                ((time - bottomBound.key) / (topBound.key - bottomBound.key)).unitlessValue
            )
        }
    }
}