package org.ghrobotics.lib.mathematics.linalg

/*
 * Some implementations and algorithms borrowed from:
 * Lo-Ellen Robotics
 * Team 4069
 */

import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.simple.SimpleMatrix

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

