package org.ghrobotics.lib.mathematics.units.derivedunits

import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.UnboundedRotation

val <T : SIValue<T>> T.acceleration: Acceleration<T> get() = Acceleration(value, this)
val Length.acceleration: LinearAcceleration get() = Acceleration(value, this)
val UnboundedRotation.acceleration: AngularAcceleration get() = Acceleration(value, this)

typealias LinearAcceleration = Acceleration<Length>
typealias AngularAcceleration = Acceleration<UnboundedRotation>

@Deprecated("")
class Acceleration<T : SIValue<T>>(
    override val value: Double,
    internal val type: T
) : SIValue<Acceleration<T>> {
    override fun createNew(newValue: Double) = Acceleration(newValue, type)

    operator fun times(other: Time) = Velocity(value * other.value, type)
}