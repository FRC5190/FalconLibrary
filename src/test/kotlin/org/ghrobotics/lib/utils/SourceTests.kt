package org.ghrobotics.lib.utils

import org.junit.Test

class SourceTests {

    @Test
    fun constTest() {
        val one = Source(5)

        assert(one() == 5)
    }

    @Test
    fun variableSourceTest() {
        var value = 5

        val one: Source<Int> = { value }

        assert(one() == 5)
        value = 2
        assert(one() == 2)
    }

    @Test
    fun sourceWithProcessingTest() {
        var value = 1

        val one: Source<Int> = { value }
        val two = one.map { it > 2 }

        assert(!two())
        value = 3
        assert(two())
    }

    @Test
    fun sourceMapTest() {
        var value = true

        val constOne = 1
        val constTwo = 2

        val one: BooleanSource = { value }
        val two = one.map(constOne, constTwo)

        assert(two() == constOne)
        value = false
        assert(two() == constTwo)
    }

    @Test
    fun sourceEqualsTest() {
        var value = false

        val one: BooleanSource = { value }
        val two = one.withEquals(true)

        assert(!two())
        value = true
        assert(two())
    }
}