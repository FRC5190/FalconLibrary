package org.ghrobotics.lib.utils

import javafx.beans.value.ObservableValue

fun <T : Any> ObservableValue<T>.addEnterListener(
    enterValue: T,
    changeListener: () -> Unit
) = addListener { _, _, newValue ->
    if (newValue == enterValue) changeListener()
}

fun <T> ObservableValue<T>.addLeaveListener(
    leaveValue: T,
    changeListener: () -> Unit
) = addListener { _, oldValue, _ ->
    if (oldValue == leaveValue) changeListener()
}

fun <T> ObservableValue<T>.addTransitionListener(
    leaveValue: T,
    enterValue: T,
    changeListener: () -> Unit
) = addListener { _, oldValue, newValue ->
    if (oldValue == leaveValue && newValue == enterValue) changeListener()
}