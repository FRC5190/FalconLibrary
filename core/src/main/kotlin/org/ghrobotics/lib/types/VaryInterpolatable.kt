package org.ghrobotics.lib.types

interface VaryInterpolatable<S> : Interpolatable<S> {
    fun distance(other: S): Double
}