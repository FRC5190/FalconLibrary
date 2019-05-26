package org.ghrobotics.lib.mathematics.linalg

/*
 * Some implementations and algorithms borrowed from:
 * Lo-Ellen Robotics
 * Team 4069
 */

import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.dense.row.NormOps_DDRM
import org.ejml.simple.SimpleMatrix

open class Matrix<R: `100`, C: `100`>(private val rows: Nat<R>, private val cols: Nat<C>, internal val storage: SimpleMatrix) {
    val numCols get() = cols.i

    val numRows get() = rows.i

    operator fun get(i: Int, j: Int) = storage[i, j]

    operator fun set(i: Int, j: Int, k: Double) {
        storage[i, j] = k
    }

    fun diag() = Matrix(rows, cols, storage.diag())
    fun maxInternal() = CommonOps_DDRM.elementMax(this.storage.ddrm)
    fun minInternal() = CommonOps_DDRM.elementMin(this.storage.ddrm)
    fun mean() = elementSum() / storage.numElements.toDouble()

    operator fun <C2: `100`> times(other: Matrix<C, C2>): Matrix<R, C2>
        = Matrix(rows, other.cols, this.storage.mult(other.storage))

    operator fun times(value: Double): Matrix<R, C>
        = Matrix(rows, cols, this.storage.scale(value))

    fun elementTimes(other: Matrix<R, C>): Matrix<R, C>
        = Matrix(rows, cols, this.storage.elementMult(other.storage))

    operator fun unaryMinus() = Matrix(rows, cols, storage.scale(-1.0))

    operator fun minus(value: Double) = Matrix(rows, cols, storage.minus(value))

    operator fun minus(value: Matrix<R, C>) = Matrix(rows, cols, storage.minus(value.storage))

    operator fun plus(value: Double) = Matrix(rows, cols, storage.plus(value))

    operator fun plus(value: Matrix<R, C>) = Matrix(rows, cols, storage.plus(value.storage))

    operator fun div(value: Int) = Matrix(rows, cols, storage.divide(value.toDouble()))

    operator fun div(value: Double) = Matrix(rows, cols, storage.divide(value))

    fun transpose(): Matrix<C, R> = Matrix(cols, rows, storage.transpose())

    fun copy() = Matrix(cols, rows, storage.copy())

    fun inv(): Matrix<R, C> = Matrix(rows, cols, storage.invert())

    fun det(): Double = storage.determinant()

    fun normF() = storage.normF()

    fun normIndP1() = NormOps_DDRM.inducedP1(this.storage.ddrm)

    fun elementSum() = this.storage.elementSum()

    fun trace() = this.storage.trace()

    fun epow(other: Double): Matrix<R, C> = Matrix(rows, cols, storage.elementPower(other))
    fun epow(other: Int): Matrix<R, C> = Matrix(rows, cols, storage.elementPower(other.toDouble()))
}
