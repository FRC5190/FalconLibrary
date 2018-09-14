package frc.team5190.lib.utils

import frc.team5190.lib.utils.observabletype.*
import org.junit.Test

class StateTests {

    @Test
    fun basicAnd() {
        val one = ObservableValue(true)
        val two = ObservableValue(true)

        val three = one and two

        assert(three.value)
    }

    @Test
    fun basicOr() {
        val one = ObservableValue(true)
        val two = ObservableValue(false)

        val three = one or two
        val four = two or one

        assert(three.value)
        assert(four.value)
    }

    @Test
    fun basicNumToBoolean() {
        val one = ObservableValue(5.0)
        val two = one.withProcessing { it > 2.5 }

        assert(two.value)
    }

    @Test
    fun variableListener() {
        val one = ObservableVariable(1.0)
        val two = one.withProcessing { it > 2.0 }

        var called = false

        two.invokeWhenTrue {
            called = true
        }

        assert(!called)
        one.value = 3.0
        assert(called)
    }

    @Test
    fun doubleVariableListener() {
        val one = ObservableVariable(1.0)
        val two = ObservableVariable(5.0)

        val three = one.withProcessing { it > 2.0 }
        val four = two.withProcessing { it < 2.0 }

        val five = three and four

        var called = false

        five.invokeWhenTrue {
            called = true
        }

        assert(!called)
        one.value = 3.0
        assert(!called)
        one.value = 1.0
        two.value = 1.0
        assert(!called)
        one.value = 3.0
        assert(called)
    }

    @Test
    fun whenListener() {
        val three = ObservableVariable(false)

        var called = false

        var handle = three.invokeWhenTrue {
            called = true
        }

        assert(!called)
        handle.dispose()

        handle = three.invokeWhenFalse {
            called = true
        }

        assert(called)
        handle.dispose()
    }

    @Test
    fun valueTest() {
        val one = ObservableVariable(false)
        val two = ObservableVariable(false)

        val three = one and two

        assert(!one.value)
        assert(!two.value)
        assert(!three.value)

        one.value = true
        assert(one.value)
        assert(!two.value)
        assert(!three.value)

        one.value = false
        two.value = true
        assert(!one.value)
        assert(two.value)
        assert(!three.value)

        one.value = true
        two.value = true
        assert(one.value)
        assert(two.value)
        assert(three.value)
    }

    @Test
    fun invokeOnce() {
        val one = ObservableVariable(true)

        var called = false

        one.invokeOnceOnChange { called = true }

        assert(!called)
        one.value = false
        assert(called)
        called = false
        one.value = true
        assert(!called)
    }

    @Test
    fun andInvokeOnce() {
        val one = ObservableVariable(false)
        val two = ObservableVariable(false)

        val three = one and two

        var called = false

        three.invokeOnceOnChange { called = true }

        assert(!called)
        two.value = false
        one.value = true
        assert(!called)
        one.value = false
        two.value = true
        assert(!called)
        one.value = true
        two.value = true
        assert(called)
        called = false
        one.value = false
        two.value = false
        assert(!called)
    }

    @Test
    fun counterState() {
        var counter = 0

        val one = UpdatableObservableValue(5) { counter++ }
        val two = one.withProcessing { it >= 5 }

        var called = false

        two.invokeWhenTrue { called = true }

        Thread.sleep(500)
        assert(!called)
        Thread.sleep(500)
        assert(called)
    }

    @Test
    fun frequencyTest() {
        var counter = 0

        val one = UpdatableObservableValue(5) { counter++ }

        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 900) {
            one.value
            Thread.sleep(1)
        }

        assert(counter == 5)
    }

    @Test
    fun recursiveListener() {
        lateinit var two: ObservableValue<Double>

        val one by lazy { two }

        val three by lazy { one.withProcessing { it > 5.0 } }

        var calledFalse = false
        var calledTrue = false

        two = object : SubscribableObservableValueImpl<Double>() {
            override val value: Double = 0.0
            override fun start() {
                three.invokeWhenFalse {
                    calledFalse = true
                }
            }
        }

        three.invokeWhenTrue {
            calledTrue = true
        }

        assert(calledFalse)
        assert(!calledTrue)
    }

    @Test
    fun notTest() {
        val one = ObservableVariable(true)
        val two = !one

        var called = false

        two.invokeWhenTrue {
            called = true
        }

        assert(!called)
        assert(!two.value)
        one.value = false
        assert(called)
        assert(two.value)
    }

    @Test
    fun comparisonTest() {
        val one = ObservableVariable(5.0)
        val two = ObservableValue(10.0)

        val three = one.greaterThan(two)
        val four = one.lessThan(13.0)

        assert(!three.value)
        assert(four.value)
        one.value = 15.0
        assert(three.value)
        assert(!four.value)
    }

    @Test
    fun referenceTest() {
        val one = UpdatableObservableValue(5) {
            Thread.sleep(100)
            false
        }
        val two = ObservableValue(true)

        val three = ObservableValueReference(one)

        var called = false

        three.invokeWhenTrue {
            called = true
        }
        assert(!called)
        three.reference = two
        assert(called)
    }

    @Test
    fun conflatedTest() {
        val points = 1000
        var current = 0

        val sensor = UpdatableObservableValue(1000) {
            current++
            if(current > points) current = 0
            current
        }

        val conflated = sensor.asConflated()

        var pointsGot = 0
        var lastPoint = 0

        current = 0

        conflated.invokeOnSet {
            if(it < lastPoint) {
                println("Got $pointsGot points out of $points points sent")
                dispose()
                return@invokeOnSet
            }
            Thread.sleep(20)
            lastPoint = it
            pointsGot++
        }

        Thread.sleep(1100)
    }

}