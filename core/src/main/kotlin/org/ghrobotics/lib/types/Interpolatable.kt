package org.ghrobotics.lib.types

interface Interpolatable<T> {

    /**
     * The function calculates an interpolated value along the fraction
     * `t` between `0.0` and `1.0`. When `t` = 1.0,
     * `endVal` is returned.
     *
     * @param endValue
     * target value
     * @param t
     * fraction between `0.0` and `1.0`
     * @return interpolated value
     */
    fun interpolate(endValue: T, t: Double): T
}