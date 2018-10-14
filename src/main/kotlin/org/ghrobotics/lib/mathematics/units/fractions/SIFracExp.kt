package org.ghrobotics.lib.mathematics.units.fractions

interface SIFracExpT1<A> {
    val tA: A
}

interface SIFracExpT2<A, B> : SIFracExpT1<A> {
    val tB: B
}

interface SIFracExpT3<A, B, C> : SIFracExpT2<A, B> {
    val tC: C
}

interface SIFracExpT4<A, B, C, D> : SIFracExpT3<A, B, C> {
    val tD: D
}

interface SIFracExpB1<A> {
    val bA: A
}

interface SIFracExpB2<A, B> : SIFracExpB1<A> {
    val bB: B
}

interface SIFracExpB3<A, B, C> : SIFracExpB2<A, B> {
    val bC: C
}

interface SIFracExpB4<A, B, C, D> : SIFracExpB3<A, B, C> {
    val bD: D
}

interface SIFracExpB5<A, B, C, D, E> : SIFracExpB4<A, B, C, D> {
    val bE: E
}