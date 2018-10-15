package org.ghrobotics.lib.mathematics.units

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.kEpsilon
import kotlin.math.absoluteValue

val Number.degree: Rotation get() = RotationImpl(toDouble(), false)
val Number.radian: Rotation get() = RotationImpl(toDouble(), true)

@Suppress("FunctionName")
fun Rotation(x: Double, y: Double, normalize: Boolean): Rotation =
    RotationImpl(x, y, normalize)

interface Rotation : SIValue<Rotation> {
    val degree: Rotation
    val radian: Rotation

    val cos: Double
    val sin: Double

    fun isParallel(rotation: Rotation): Boolean

    companion object {
        val kRotation = 360.degree
    }
}

class RotationImpl : AbstractSIValue<Rotation>, Rotation {
    override val asDouble: Double
    private val isRadian: Boolean
    override val cos: Double
    override val sin: Double

    constructor(value: Double, isRadian: Boolean) : super() {
        this.asDouble = value
        this.isRadian = isRadian

        val radianAngle = if (isRadian) value else Math.toRadians(value)
        cos = Math.cos(radianAngle)
        sin = Math.sin(radianAngle)
    }

    constructor(x: Double, y: Double, normalize: Boolean) : super() {
        if (normalize) {
            val magnitude = Math.hypot(x, y)
            if (magnitude > kEpsilon) {
                sin = y / magnitude
                cos = x / magnitude
            } else {
                sin = 0.0
                cos = 1.0
            }
        } else {
            cos = x
            sin = y
        }
        isRadian = true
        asDouble = Math.atan2(sin, cos) % (Math.PI * 2)
    }

    override val asMetric get() = radian
    override val absoluteValue get() = RotationImpl(asDouble.absoluteValue, isRadian)

    override fun unaryMinus() = RotationImpl(cos, -sin, false)

    override fun plus(other: Rotation) = RotationImpl(
        cos * other.cos - sin * other.sin,
        cos * other.sin + sin * other.cos,
        true
    )

    override fun minus(other: Rotation) = plus(-other)

    override fun div(other: Rotation) = radian.asDouble / other.radian.asDouble

    override fun times(other: Number) = RotationImpl(asDouble * other.toDouble(), isRadian)
    override fun div(other: Number) = RotationImpl(asDouble / other.toDouble(), isRadian)

    override fun compareTo(other: Rotation) = radian.asDouble.compareTo(other.asDouble)

    override val degree get() = if (isRadian) RotationImpl(Math.toDegrees(asDouble), false) else this
    override val radian get() = if (!isRadian) RotationImpl(Math.toRadians(asDouble), true) else this

    override fun isParallel(rotation: Rotation) = (this - rotation).asDouble epsilonEquals 0.0
}