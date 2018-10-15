package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.AbstractSIValue
import org.ghrobotics.lib.mathematics.units.SIValue
import kotlin.math.absoluteValue

fun <T : SIValue<T>> T.fromModel(model: NativeUnitModel<T>) = model.fromModel(this)

val Number.STU: NativeUnit get() = NativeUnitImpl(toDouble())

interface NativeUnit :
    SIValue<NativeUnit> {
    fun <T : SIValue<T>> toModel(model: NativeUnitModel<T>): T

    fun <T : SIValue<T>> plus(other: T, model: NativeUnitModel<T>): NativeUnit
    fun <T : SIValue<T>> minus(other: T, model: NativeUnitModel<T>): NativeUnit
}

class NativeUnitImpl(value: Double) : AbstractSIValue<NativeUnit>(), NativeUnit {
    override val asDouble: Double = value

    override val asMetric: NativeUnit = this

    override fun <T : SIValue<T>> toModel(model: NativeUnitModel<T>): T = model.toModel(this)

    override val absoluteValue by lazy { NativeUnitImpl(value.absoluteValue) }

    override fun plus(other: NativeUnit) =
        NativeUnitImpl(asDouble + other.asDouble)

    override fun minus(other: NativeUnit) =
        NativeUnitImpl(asDouble - other.asDouble)

    override fun <T : SIValue<T>> plus(other: T, model: NativeUnitModel<T>): NativeUnit = plus(model.fromModel(other))

    override fun <T : SIValue<T>> minus(other: T, model: NativeUnitModel<T>): NativeUnit = minus(-other, model)

    override fun times(other: Number) =
        NativeUnitImpl(asDouble * other.toDouble())

    override fun div(other: Number) =
        NativeUnitImpl(asDouble * other.toDouble())

    override fun div(other: NativeUnit) = asDouble / other.asDouble

    override fun unaryMinus() = NativeUnitImpl(-asDouble)

    override fun compareTo(other: NativeUnit) = asDouble.compareTo(other.asDouble)
}