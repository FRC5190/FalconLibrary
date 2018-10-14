package org.ghrobotics.lib.utils.observabletype

fun <T : Comparable<F>, F> ObservableValue<T>.greaterThan(other: F) = greaterThan(ObservableValue(other))
fun <T : Comparable<F>, F> ObservableValue<T>.greaterThanOrEquals(other: F) = greaterThanOrEquals(ObservableValue(other))
fun <T : Comparable<F>, F> ObservableValue<T>.lessThan(other: F) = lessThan(ObservableValue(other))
fun <T : Comparable<F>, F> ObservableValue<T>.lessThanOrEquals(other: F) = lessThanOrEquals(ObservableValue(other))
fun <T : Comparable<F>, F> ObservableValue<T>.compareTo(other: F) = compareTo(ObservableValue(other))

fun <T : Comparable<F>, F> ObservableValue<T>.greaterThan(other: ObservableValue<F>) = compareToInternal(other) { it > 0 }
fun <T : Comparable<F>, F> ObservableValue<T>.greaterThanOrEquals(other: ObservableValue<F>) = compareToInternal(other) { it >= 0 }
fun <T : Comparable<F>, F> ObservableValue<T>.lessThan(other: ObservableValue<F>) = compareToInternal(other) { it < 0 }
fun <T : Comparable<F>, F> ObservableValue<T>.lessThanOrEquals(other: ObservableValue<F>) = compareToInternal(other) { it <= 0 }
fun <T : Comparable<F>, F> ObservableValue<T>.compareTo(other: ObservableValue<F>) = compareToInternal(other) { it }

private inline fun <T : Comparable<F>, F, R> ObservableValue<T>.compareToInternal(
        other: ObservableValue<F>,
        crossinline block: (Int) -> R
): ObservableValue<R> = this.mergeWith(other) { one, two -> block(one.compareTo(two)) }