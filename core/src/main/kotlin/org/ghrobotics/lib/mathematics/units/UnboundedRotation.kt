package org.ghrobotics.lib.mathematics.units

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d

val Number.radian get() = UnboundedRotation(toDouble())
val Number.degree get() = Math.toRadians(toDouble()).radian

@Deprecated("")
class UnboundedRotation(override val value: Double) : SIUnit<UnboundedRotation> {

    val radian get() = value // should be between -PI and PI already. // % (Math.PI * 2)
    val degree get() = Math.toDegrees(value)

    override fun minus(other: UnboundedRotation) = plus(-other)

    override fun createNew(newValue: Double) = UnboundedRotation(newValue)

    override fun equals(other: Any?) = other is UnboundedRotation && this.value epsilonEquals other.value

    override fun hashCode() = this.value.hashCode()

    fun toRotation2d() = Rotation2d(value)

    companion object {
        val kZero = UnboundedRotation(0.0)
        val kRotation = 360.degree
    }
}