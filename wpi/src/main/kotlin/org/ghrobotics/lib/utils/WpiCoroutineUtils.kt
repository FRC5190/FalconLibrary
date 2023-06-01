/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.ghrobotics.lib.wrappers.FalconNotifier
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.launchFrequency(
    frequency: Int = 50,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    if (frequency <= 0) throw IllegalArgumentException("Frequency cannot be lower then 1!")
    return launch(context, start) {
        loopFrequency(frequency, block)
    }
}

suspend fun CoroutineScope.loopFrequency(
    frequency: Int = 50,
    block: suspend CoroutineScope.() -> Unit,
) {
    val notifier = FalconNotifier(frequency)
    notifier.updateAlarm()

    while (isActive) {
        notifier.waitForAlarm()
        block(this)
        notifier.updateAlarm()
    }
}
