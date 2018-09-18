package org.ghrobotics

import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.networktables.NetworkTableInstance
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object Communications {
    val networkTableInstance = NetworkTableInstance.getDefault()!!
    val liveDashboardTable = networkTableInstance.getTable("Live Dashboard")!!

    init {
        //networkTableInstance.startClientTeam(5190)
        networkTableInstance.startClient("localhost")
    }

    var reset by entry("Reset").booleanDelegate(false)

    val startingPosition by entry("Starting Position").stringDelegate("Left")
    val nearScaleAutoMode by entry("Near Scale Auto Mode").stringDelegate("ThreeCube")
    val farScaleAutoMode by entry("Far Scale Auto Mode").stringDelegate("ThreeCube")

    val robotX by entry("Robot X").doubleDelegate(1.5)
    val robotY by entry("Robot Y").doubleDelegate(Double.NaN)
    val robotHeading by entry("Robot Heading").doubleDelegate(Double.NaN)

    val pathX by entry("Path X").doubleDelegate(1.5)
    val pathY by entry("Path Y").doubleDelegate(Double.NaN)
    val pathHeading by entry("Path Heading").doubleDelegate(Double.NaN)

    val effectiveRobotY: Double
        get() = fixY(robotY)

    val effectiveRobotHeading: Double
        get() = fixHeading(robotHeading)

    val effectivePathY: Double
        get() = fixY(pathY)

    val effectivePathHeading: Double
        get() = fixHeading(pathHeading)

    private fun fixY(value: Double): Double {
        if (!value.isNaN()) return value
        val defaultRobotY = 23.5
        return when (startingPosition) {
            "Right" -> 27.0 - defaultRobotY
            "Center" -> 13.25
            else -> defaultRobotY
        }
    }

    private fun fixHeading(value: Double): Double {
        if (!value.isNaN()) return value
        val defaultHeading = Math.PI
        return when (startingPosition) {
            "Center" -> 0.0
            else -> defaultHeading
        }
    }

    private fun entry(key: String) = liveDashboardTable.getEntry(key)!!
}

private fun NetworkTableEntry.stringDelegate(default: String) = delegate({ getString(default) }) { setString(it) }
private fun NetworkTableEntry.booleanDelegate(default: Boolean) = delegate({ getBoolean(default) }) { setBoolean(it) }
private fun NetworkTableEntry.doubleDelegate(default: Double) = delegate({ getDouble(default) }) { setDouble(it) }

private fun <T> delegate(readBlock: () -> T, writeBlock: (T) -> Unit) = object : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>) = readBlock()
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = writeBlock(value)
}