package org.ghrobotics.lib.localization

import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.types.Interpolatable
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.map
import java.util.*

class TimeInterpolatableBuffer<T : Interpolatable<T>>(
    private val historySpan: Double = 1.0,
    private val timeSource: Source<Double> = Timer::getFPGATimestamp
) {

    constructor(
        historySpan: Time,
        timeSource: Source<Time>
    ) : this(historySpan.value, timeSource.map { it.value })

    private val bufferMap = TreeMap<Double, T>()

    operator fun set(time: Time, value: T) = set(time.second, value)

    operator fun set(time: Double, value: T): T? {
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

    operator fun get(time: Time) = get(time.second)

    operator fun get(time: Double): T? {
        if (bufferMap.isEmpty()) return null

        bufferMap[time]?.let { return it }

        val topBound = bufferMap.ceilingEntry(time)
        val bottomBound = bufferMap.floorEntry(time)

        return when {
            topBound == null -> bottomBound.value
            bottomBound == null -> topBound.value
            else -> bottomBound.value.interpolate(
                topBound.value,
                (time - bottomBound.key) / (topBound.key - bottomBound.key)
            )
        }
    }
}