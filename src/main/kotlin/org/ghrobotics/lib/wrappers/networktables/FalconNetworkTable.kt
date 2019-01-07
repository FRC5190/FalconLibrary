package org.ghrobotics.lib.wrappers.networktables

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import org.ghrobotics.lib.utils.capitalizeEachWord
import kotlin.reflect.KProperty

object FalconNetworkTable {
    val instance: NetworkTableInstance = NetworkTableInstance.getDefault()

    operator fun get(name: String): NetworkTableEntry = getEntry(name)
    fun getEntry(name: String): NetworkTableEntry = instance.getEntry(name)

    fun getTable(name: String): NetworkTable = instance.getTable(name)
}

operator fun NetworkTable.get(name: String): NetworkTableEntry = getEntry(name)

fun NetworkTableEntry.delegate(defaultValue: String = ""): NetworkTableEntryDelegate<String> =
    delegate { this.getString(defaultValue) }

fun NetworkTableEntry.delegate(defaultValue: Double = 0.0): NetworkTableEntryDelegate<Double> =
    delegate { this.getDouble(defaultValue) }

fun NetworkTableEntry.delegate(defaultValue: Boolean = false): NetworkTableEntryDelegate<Boolean> =
    delegate { this.getBoolean(defaultValue) }

private fun <T> NetworkTableEntry.delegate(get: () -> T) = NetworkTableEntryDelegate(this, get)

class NetworkTableEntryDelegate<T>(
    private val entry: NetworkTableEntry,
    private val get: () -> T
) {
    operator fun setValue(networkInterface: Any, property: KProperty<*>, value: T) {
        entry.setValue(value)
    }

    operator fun getValue(networkInterface: Any, property: KProperty<*>): T = get()
}

inline fun <T> sendableChooser(crossinline block: SendableChooser<T>.() -> Unit) =
    SendableChooser<T>().apply(block)

inline fun <reified T : Enum<T>> enumSendableChooser(
    crossinline block: (T) -> String = { it.name.replace('_', ' ').capitalizeEachWord() }
) = sendableChooser<T> {
    enumValues<T>().forEach { enumValue ->
        addOption(block(enumValue), enumValue)
    }
}
