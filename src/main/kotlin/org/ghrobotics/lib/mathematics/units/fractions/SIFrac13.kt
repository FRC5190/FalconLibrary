package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.expressions.SIExp3

open class SIFrac13<T : SIValue<T>, BA : SIValue<BA>, BB : SIValue<BB>, BC : SIValue<BC>>(
    top: T,
    bottom: SIExp3<BA, BB, BC>
) : AbstractSIFrac<T, SIExp3<BA, BB, BC>, SIFrac13<T, BA, BB, BC>>(
    top,
    bottom
), SIFracExpT1<T>, SIFracExpB3<BA, BB, BC> {
    override val tA: T get() = top
    override val bA: BA get() = bottom.a
    override val bB: BB get() = bottom.b
    override val bC: BC get() = bottom.c

    override fun create(newTop: T, newBottom: SIExp3<BA, BB, BC>) = SIFrac13(newTop, newBottom)

    override fun div(other: SIFrac13<T, BA, BB, BC>): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    infix fun <D : SIUnit<D, *>> per(other: D) {

    }
}


