package org.ghrobotics.lib.utils

import java.util.*

fun <T> Collection<T>.plusToSet(other: Collection<T>): Set<T> {
    val result = HashSet<T>(size + other.size)
    result.addAll(this)
    result.addAll(other)
    return result
}

fun <T> Collection<T>.plusToSet(other: Array<out T>): Set<T> {
    val result = HashSet<T>(size + other.size)
    result.addAll(this)
    result.addAll(other)
    return result
}

inline fun <T, R> Collection<T>.mapToSet(transform: (T) -> R): Set<R> = mapTo(HashSet(size), transform)

inline fun <T, R> Collection<T>.flatMapToSet(multiplier: Int, transform: (T) -> Iterable<R>): Set<R> =
    flatMapTo(HashSet(size * multiplier), transform)

inline fun <T, R : Any> Collection<T>.mapNotNullToSet(transform: (T) -> R?): Set<R> =
    mapNotNullTo(HashSet(size), transform)

inline fun <T> Collection<T>.filterNotToSet(predicate: (T) -> Boolean): Set<T> = filterNotTo(HashSet(size), predicate)

fun <T> Collection<T>.combinationPairs(): Set<Pair<T, T>> {
    val result = HashSet<Pair<T, T>>(size)
    for (p1 in this) {
        for (p2 in this) {
            if (p1 == p2 || result.contains(p2 to p1)) continue
            result += p1 to p2
        }
    }
    return result
}