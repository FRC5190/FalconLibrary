package org.ghrobotics.lib.subsystems.drive.localization

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import java.util.*

class InterpolatableLocalizationBuffer(private val maxSize: Int = 100) : TreeMap<Double, Pose2d>() {

    override fun put(key: Double, value: Pose2d): Pose2d? {
        if (super.size >= maxSize) {
            remove(firstKey())
        }

        @Suppress("ReplacePutWithAssignment") // it's not possible to replace with assignment
        super.put(key, value)

        return value
    }

    fun getInterpolated(key: Double): Pose2d? {
        return super.get(key) ?: {
            val topBound = ceilingKey(key)
            val bottomBound = floorKey(key)

            when {
                topBound == null -> get(bottomBound)
                bottomBound == null -> get(topBound)
                else -> {
                    val topElem = get(topBound)
                    val bottomElem = get(bottomBound)

                    val upperToLower = topBound - bottomBound
                    val keyToLower = key - bottomBound

                    val t = when {
                        upperToLower <= 0 -> 0.0
                        keyToLower <= 0 -> 0.0
                        else -> keyToLower / upperToLower
                    }
                    bottomElem!!.interpolate(topElem!!, t)
                }
            }
        }()
    }
}