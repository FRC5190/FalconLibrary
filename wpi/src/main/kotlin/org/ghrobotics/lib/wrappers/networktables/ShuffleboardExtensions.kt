/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

/*
 * Copyright 2019 Lo-Ellen Robotics
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SaturnLibrary can be found online at https://github.com/FRCTeam4069/SaturnLibrary
 */

package org.ghrobotics.lib.wrappers.networktables

import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.wpilibj.Sendable
import edu.wpi.first.wpilibj.shuffleboard.*
import org.ghrobotics.lib.utils.Source

fun tab(name: String, block: ShuffleboardTabBuilder.() -> Unit)
    = ShuffleboardTabBuilder(name).apply(block).build()

class FalconShuffleboardTab(val tab: ShuffleboardTab, updateBlock: () -> Unit): Updating(updateBlock)

class ShuffleboardTabBuilder(name: String) {

    val tab: ShuffleboardTab = Shuffleboard.getTab(name)

    internal fun build() = FalconShuffleboardTab(tab, this::update)

    private val updatableComponents: ArrayList<Updating> = arrayListOf()

    private fun update() {
        updatableComponents.forEach(Updating::update)
    }

    fun textView(name: String, value: () -> Any, block: ShuffleboardWidgetBuilder<String>.() -> Unit)
        = ShuffleboardWidgetBuilder(tab.addString(name) { value().toString() }.withWidget(BuiltInWidgets.kTextView))
            .apply(block)
            .build()

    fun numberSlider(name: String, value: () -> Number, block: ShuffleboardWidgetBuilder<Double>.() -> Unit)
        = ShuffleboardWidgetBuilder(tab.addNumber(name) { value().toDouble() }.withWidget(BuiltInWidgets.kNumberSlider))
            .apply(block)
            .build()

    fun numberBar(name: String, value: () -> Number, block: ShuffleboardWidgetBuilder<Double>.() -> Unit)
        = ShuffleboardWidgetBuilder(tab.addNumber(name) { value().toDouble() }.withWidget(BuiltInWidgets.kNumberBar))
            .apply(block)
            .build()

    fun dial(name: String, value: () -> Number, block: ShuffleboardWidgetBuilder<Double>.() -> Unit)
        = ShuffleboardWidgetBuilder(tab.addNumber(name) { value().toDouble() }.withWidget(BuiltInWidgets.kDial))
            .apply(block)
            .build()

    fun booleanBox(name: String, value: () -> Boolean, block: ShuffleboardWidgetBuilder<Boolean>.() -> Unit)
        = ShuffleboardWidgetBuilder(tab.addBoolean(name, value).withWidget(BuiltInWidgets.kBooleanBox))
            .apply(block)
            .build()

    fun toggleButton(name: String, value: () -> Boolean, block: ShuffleboardWidgetBuilder<Boolean>.() -> Unit)
        = ShuffleboardWidgetBuilder(tab.addBoolean(name, value).withWidget(BuiltInWidgets.kToggleButton))
            .apply(block)
            .build()

    fun toggleSwitch(name: String, value: () -> Boolean, block: ShuffleboardWidgetBuilder<Boolean>.() -> Unit)
        = ShuffleboardWidgetBuilder(tab.addBoolean(name, value).withWidget(BuiltInWidgets.kToggleSwitch))
            .apply(block)
            .build()

    fun voltageView(name: String, value: () -> Double, block: ShuffleboardWidgetBuilder<Double>.() -> Unit)
        = ShuffleboardWidgetBuilder(tab.addNumber(name, value).withWidget(BuiltInWidgets.kVoltageView))
            .apply(block)
            .build()

    fun layout(name: String, layoutType: LayoutType, block: ShuffleboardLayoutBuilder.() -> Unit) =
        ShuffleboardLayoutBuilder(tab.getLayout(name, layoutType))
            .apply(block)
            .build()
            .apply { updatableComponents.add(this) }

    fun list(name: String, block: ShuffleboardLayoutBuilder.() -> Unit)
        = layout(name, BuiltInLayouts.kList, block)

    fun grid(name: String, block: ShuffleboardLayoutBuilder.() -> Unit) = layout(name, BuiltInLayouts.kGrid, block)
}

open class Updating(open val updateBlock: () -> Unit) {
    fun update() { updateBlock() }
}

class ShuffleboardWidgetBuilder<T>(val widget: SuppliedValueWidget<T>) {
    fun position(column: Int, row: Int) {
        widget.withPosition(column, row)
    }

    fun size(width: Int, height: Int) {
        widget.withSize(width, height)
    }

    fun properties(vararg props: Pair<String, Any>) {
        widget.withProperties(mapOf(*props))
    }

    fun build(): SuppliedValueWidget<T> {
        return widget
    }
}

class FalconShuffleboardProperty(widget: SimpleWidget,
                                 val update: FalconShuffleboardProperty.() -> Unit): () -> Unit {
    val entry: NetworkTableEntry = widget.entry

    override fun invoke() {
        update()
    }
}

class FalconShuffleboardLayout(val layout: ShuffleboardLayout, updateBlock: () -> Unit): Updating(updateBlock)

class ShuffleboardLayoutBuilder(val layout: ShuffleboardLayout) {
    fun position(column: Int, row: Int) {
        layout.withPosition(column, row)
    }

    fun size(width: Int, height: Int) {
        layout.withSize(width, height)
    }

    fun properties(vararg props: Pair<String, Any>) {
        layout.withProperties(mapOf(*props))
    }

    internal var updatableComponents: ArrayList<() -> Unit> = arrayListOf()

    fun send(name: String, sendable: Sendable): ComplexWidget = layout.add(name, sendable)

    fun property(name: String, defaultValue: Any, block: FalconShuffleboardProperty.() -> Unit) =
        FalconShuffleboardProperty(layout.add(name, defaultValue), block).apply { updatableComponents.add(this) }

    fun doubleProperty(name: String, value: Source<Double>) = property(name, value()) {
        entry.setDouble(value())
    }

    fun numberProperty(name: String, value: Source<Number>) = property(name, value()) {
        entry.setNumber(value())
    }

    fun booleanProperty(name: String, value: Source<Boolean>) = property(name, value()) {
        entry.setBoolean(value())
    }

    fun booleanArrayProperty(name: String, value: Source<Array<out Boolean>>) = property(name, value()) {
        entry.setBooleanArray(value())
    }

    fun numberArrayProperty(name: String, value: Source<Array<out Number>>) = property(name, value()) {
        entry.setNumberArray(value())
    }

    fun stringProperty(name: String, value: Source<String>) = property(name, value()) {
        entry.setString(value())
    }

    fun stringArrayProperty(name: String, value: Source<Array<out String>>) = property(name, value()) {
        entry.setStringArray(value())
    }

    fun build() = FalconShuffleboardLayout(layout) { updatableComponents.forEach { it.invoke() } }
}