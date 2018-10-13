package org.ghrobotics.lib.types

import javafx.animation.Interpolatable

interface VaryInterpolatable<S> : Interpolatable<S> {
    fun distance(other: S): Double
}