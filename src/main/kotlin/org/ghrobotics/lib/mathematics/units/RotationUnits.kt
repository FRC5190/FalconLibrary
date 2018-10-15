package org.ghrobotics.lib.mathematics.units

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.kEpsilon
import kotlin.math.absoluteValue

val Number.degree: Rotation2d get() = Rotation2dImpl(toDouble(), false)
val Number.radian: Rotation2d get() = Rotation2dImpl(toDouble(), true)

interface Rotation2d : SIValue<Rotation2d> {
    val degree: Rotation2d
    val radian: Rotation2d

    val cos: Double
    val sin: Double

    fun isParallel(rotation: Rotation2d): Boolean

    companion object {
        val kRotation = 360.degree
    }
}

class Rotation2dImpl : AbstractSIValue<Rotation2d>, Rotation2d {
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
    override val absoluteValue get() = Rotation2dImpl(asDouble.absoluteValue, isRadian)

    override fun unaryMinus() = Rotation2dImpl(cos, -sin, false)

    override fun plus(other: Rotation2d) = Rotation2dImpl(
        cos * other.cos - sin * other.sin,
        cos * other.sin + sin * other.cos,
        true
    )

    override fun minus(other: Rotation2d) = plus(-other)

    override fun div(other: Rotation2d) = radian.asDouble / other.radian.asDouble

    override fun times(other: Number) = Rotation2dImpl(asDouble * other.toDouble(), isRadian)
    override fun div(other: Number) = Rotation2dImpl(asDouble / other.toDouble(), isRadian)

    override fun compareTo(other: Rotation2d) = radian.asDouble.compareTo(other.asDouble)

    override val degree get() = if (isRadian) Rotation2dImpl(Math.toDegrees(asDouble), false) else this
    override val radian get() = if (!isRadian) Rotation2dImpl(Math.toRadians(asDouble), true) else this

    override fun isParallel(rotation: Rotation2d) = (this - rotation).asDouble epsilonEquals 0.0
}