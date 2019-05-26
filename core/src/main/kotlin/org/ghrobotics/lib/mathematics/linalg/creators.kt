package org.ghrobotics.lib.mathematics.linalg

/*
 * Some implementations and algorithms borrowed from:
 * Lo-Ellen Robotics
 * Team 4069
 */

import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.simple.SimpleMatrix

object mat {
    operator fun <R : `100`, C : `100`> get(rows: Nat<R>, cols: Nat<C>, vararg data: Double): Matrix<R, C> {
        if (data.size != rows.i * cols.i) {
            throw IllegalArgumentException("Invalid matrix data provided. Wanted ${rows.i} x ${cols.i} matrix, but got ${data.size} elements")
        }

        val matData = data.toList().chunked(cols.i).map { it.toDoubleArray() }.toTypedArray()
        return Matrix(rows, cols, SimpleMatrix(matData))
    }

    operator fun <R : `100`, C : `100`> get(rows: Nat<R>, cols: Nat<C>, vararg data: Int): Matrix<R, C> {
        if (data.size != rows.i * cols.i) {
            throw IllegalArgumentException("Invalid matrix data provided. Wanted ${rows.i} x ${cols.i} matrix, but got ${data.size} elements")
        }

        val matData = data.toList().map { it.toDouble() }.chunked(cols.i).map { it.toDoubleArray() }.toTypedArray()
        return Matrix(rows, cols, SimpleMatrix(matData))
    }
}

object vec {
    operator fun <D : `100`> get(dim: Nat<D>, vararg data: Double): Matrix<D, `1`> {
        if (data.size != dim.i) {
            throw IllegalArgumentException("Invalid number of elements for ${dim.i}-dimensional vector. got ${data.size} elements")
        }

        return Matrix(dim, `1`, SimpleMatrix(dim.i, 1, false, data))
    }

    operator fun <D : `100`> get(dim: Nat<D>, vararg data: Int): Matrix<D, `1`> {
        if (data.size != dim.i) {
            throw IllegalArgumentException("Invalid number of elements for ${dim.i}-dimensional vector. got ${data.size} elements")
        }

        return Matrix(dim, `1`, SimpleMatrix(dim.i, 1, false, data.map { it.toDouble() }.toDoubleArray()))
    }
}

fun <R: `100`, C: `100`> zeros(rows: Nat<R>, cols: Nat<C>) = Matrix(rows, cols, SimpleMatrix(rows.i, cols.i))
fun <D: `100`> zeros(size: Nat<D>) = Matrix(size, `1`, SimpleMatrix(size.i, 1))

fun <D: `100`> eye(size: Nat<D>) = Matrix(size, size, SimpleMatrix.identity(size.i))

fun <R: `100`, C: `100`> ones(rows: Nat<R>, cols: Nat<C>): Matrix<R, C> {
    val out = SimpleMatrix(rows.i, cols.i)
    CommonOps_DDRM.fill(out.ddrm, 1.0)
    return Matrix(rows, cols, out)
}
fun <D: `100`> ones(size: Nat<D>): Matrix<D, `1`> {
    val out = SimpleMatrix(size.i, 1)
    CommonOps_DDRM.fill(out.ddrm, 1.0)

    return Matrix(size, `1`, out)
}

