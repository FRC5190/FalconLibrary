/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers

import edu.wpi.first.hal.NotifierJNI
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.mathematics.units.*
import java.util.concurrent.TimeUnit

/**
 * A minimal wrapper over FPGA notifier alarms for periodic behaviour
 *
 * Unlike wpilib's [Notifier], this alarm does not spawn a new thread, nor does it take a task to run.
 * Rather it provides the functionality for FPGA-backed timing, and leaves task execution to the caller
 */
class FalconNotifier(frequency: Int) : AutoCloseable {

    /**
     * The period of this alarm in microseconds
     */
    private val period = TimeUnit.SECONDS.toMicros(1) / frequency

    /**
     * The JNI handle for this alarm
     */
    private val notifier = NotifierJNI.initializeNotifier()

    private var closed = false

    /**
     * Updates the notifier alarm. Should be called when the alarm is tripped to
     * reset the ticker
     */
    fun updateAlarm(currentTime: SIUnit<Second> = Timer.getFPGATimestamp().seconds) {
        if (closed) {
            throw IllegalStateException("updateAlarm() called on a disposed notifier! Check usages of close() for the relevant instance")
        }
        NotifierJNI.updateNotifierAlarm(notifier, (currentTime.microsecond + period).toLong())
    }

    /**
     * Blocks the thread until the notifier alarm trips.
     */
    fun waitForAlarm(): Long {
        if (closed) {
            throw IllegalStateException("waitForAlarm() called on a disposed notifier! Check usages of close() for the relevant instance")
        }
        return NotifierJNI.waitForNotifierAlarm(notifier)
    }

    /**
     * Disposes of the internal notifier handle, and **invalidates** the associated [FalconNotifier]
     *
     * This should be the last function called before the instance goes out of scope for garbage collection.
     * Alarm resetting should not be done if the notifier has been closed.
     */
    override fun close() {
        closed = true
        NotifierJNI.cleanNotifier(notifier)
    }
}