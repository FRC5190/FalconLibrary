package org.ghrobotics.lib.mathematics.linalg

/*
 * Some implementations and algorithms borrowed from:
 * Lo-Ellen Robotics
 * Team 4069
 */

typealias Vector<D> = Matrix<D, `1`>

operator fun <D: `100`> Matrix<D, `1`>.get(i: Int) = storage[i, 0]
operator fun <D: `100`> Matrix<D, `1`>.set(i: Int, j: Double) {
    storage[i, 0] = j
}