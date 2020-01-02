/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers.networktables

import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj.controller.PIDController
import edu.wpi.first.wpilibj.shuffleboard.* // ktlint-disable no-wildcard-imports
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.PIDCommand
import org.ghrobotics.lib.utils.Source


/**
 * A helper DSL to create ShuffleBoard layouts. For example:
 *
 * <pre>
 * {@code
 * val chooser = enumSendableChooser<StartingPositions>()
 * val tab = tab("Croissant") {
 *   sendableChooser("Starting Position", chooser) {
 *     position(row = 0, column = 0)
 *     size(width = 2, height = 1)
 *   }
 *   list("angles") {
 *     position(column = 1, row = 0)
 *     size(width = 2, height = 4)
 *     double("FL angle (deg)" { DriveSubsystem.flModule.angle.degrees }),
 *     double("FR angle (deg)" { DriveSubsystem.frModule.angle.degrees }),
 *     double("BL angle (deg)" { DriveSubsystem.blModule.angle.degrees }),
 *     double("BR angle (deg)" { DriveSubsystem.brModule.angle.degrees })
 *   }
 *   list("Azimuth angle controllers") {
 *     position(column = 2, row = 0)
 *     size(width = 2, height = 4)
 *     sendable("FL controller", DriveSubsystem.flModule.angleController)
 *     sendable("FR controller", DriveSubsystem.flModule.angleController)
 *     sendable("BL controller", DriveSubsystem.flModule.angleController)
 *     sendable("BR controller", DriveSubsystem.flModule.angleController)
 *   }
 *   textView("A number", { Math.random() }) {
 *     position(column = 4, row = 0)
 *     size(width = 1, height = 1)
 *   }
 * }
 * }
 </pre>
 *
 */
fun tab(name: String, block: ShuffleboardTabBuilder.() -> Unit) =
    ShuffleboardTabBuilder(name).apply(block).build()

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

/**
 * A helper DSL to create ShuffleBoard layouts. For example:
 *
 * <pre>
 * {@code
 * val chooser = enumSendableChooser<StartingPositions>()
 * val tab = tab("Croissant") {
 *   sendableChooser("Starting Position", chooser) {
 *     position(row = 0, column = 0)
 *     size(width = 2, height = 1)
 *   }
 *   list("angles") {
 *     position(column = 1, row = 0)
 *     size(width = 2, height = 4)
 *     double("FL angle (deg)" { DriveSubsystem.flModule.angle.degrees }),
 *     double("FR angle (deg)" { DriveSubsystem.frModule.angle.degrees }),
 *     double("BL angle (deg)" { DriveSubsystem.blModule.angle.degrees }),
 *     double("BR angle (deg)" { DriveSubsystem.brModule.angle.degrees })
 *   }
 *   list("Azimuth angle controllers") {
 *     position(column = 2, row = 0)
 *     size(width = 2, height = 4)
 *     sendable("FL controller", DriveSubsystem.flModule.angleController)
 *     sendable("FR controller", DriveSubsystem.flModule.angleController)
 *     sendable("BL controller", DriveSubsystem.flModule.angleController)
 *     sendable("BR controller", DriveSubsystem.flModule.angleController)
 *   }
 *   textView("A number", { Math.random() }) {
 *     position(column = 4, row = 0)
 *     size(width = 1, height = 1)
 *   }
 * }
 * }
</pre>
 *
 */
class ShuffleboardTabBuilder(name: String) {

    private val tab: ShuffleboardTab = Shuffleboard.getTab(name)

    internal fun build() = tab

    fun textView(name: String, value: () -> Any, block: ShuffleboardWidgetBuilder<String>.() -> Unit) =
        ShuffleboardWidgetBuilder(tab.addString(name) { value().toString() }.withWidget(BuiltInWidgets.kTextView))
            .apply(block)
            .build()

    fun numberSlider(name: String, value: () -> Number, block: ShuffleboardWidgetBuilder<Double>.() -> Unit) =
        ShuffleboardWidgetBuilder(tab.addNumber(name) { value().toDouble() }.withWidget(BuiltInWidgets.kNumberSlider))
            .apply(block)
            .build()

    fun numberBar(name: String, value: () -> Number, block: ShuffleboardWidgetBuilder<Double>.() -> Unit) =
        ShuffleboardWidgetBuilder(tab.addNumber(name) { value().toDouble() }.withWidget(BuiltInWidgets.kNumberBar))
            .apply(block)
            .build()

    fun dial(name: String, value: () -> Number, block: ShuffleboardWidgetBuilder<Double>.() -> Unit) =
        ShuffleboardWidgetBuilder(tab.addNumber(name) { value().toDouble() }.withWidget(BuiltInWidgets.kDial))
            .apply(block)
            .build()

    fun booleanBox(name: String, value: () -> Boolean, block: ShuffleboardWidgetBuilder<Boolean>.() -> Unit) =
        ShuffleboardWidgetBuilder(tab.addBoolean(name, value).withWidget(BuiltInWidgets.kBooleanBox))
            .apply(block)
            .build()

    fun toggleButton(name: String, value: () -> Boolean, block: ShuffleboardWidgetBuilder<Boolean>.() -> Unit) =
        ShuffleboardWidgetBuilder(tab.addBoolean(name, value).withWidget(BuiltInWidgets.kToggleButton))
            .apply(block)
            .build()

    fun toggleSwitch(name: String, value: () -> Boolean, block: ShuffleboardWidgetBuilder<Boolean>.() -> Unit) =
        ShuffleboardWidgetBuilder(tab.addBoolean(name, value).withWidget(BuiltInWidgets.kToggleSwitch))
            .apply(block)
            .build()

    fun voltageView(name: String, value: () -> Double, block: ShuffleboardWidgetBuilder<Double>.() -> Unit) =
        ShuffleboardWidgetBuilder(tab.addNumber(name, value).withWidget(BuiltInWidgets.kVoltageView))
            .apply(block)
            .build()

    fun pdpView(name: String, value: PowerDistributionPanel, block: ShuffleboardComplexWidgetBuilder.() -> Unit) =
        ShuffleboardComplexWidgetBuilder(tab.add(name, value).withWidget(BuiltInWidgets.kPowerDistributionPanel))
            .apply(block)
            .build()

    fun <T> sendableChooser(name: String, value: SendableChooser<T>, block: ShuffleboardComplexWidgetBuilder.() -> Unit) =
        ShuffleboardComplexWidgetBuilder(tab.add(name, value).withWidget(BuiltInWidgets.kComboBoxChooser))
            .apply(block)
            .build()

    fun encoder(name: String, value: Encoder, block: ShuffleboardComplexWidgetBuilder.() -> Unit) =
        ShuffleboardComplexWidgetBuilder(tab.add(name, value).withWidget(BuiltInWidgets.kEncoder))
            .apply(block)
            .build()

    fun <T> speedController(name: String, value: T, block: ShuffleboardComplexWidgetBuilder.() -> Unit)
        where T : SpeedController, T: Sendable =
        ShuffleboardComplexWidgetBuilder(tab.add(name, value).withWidget(BuiltInWidgets.kSpeedController))
            .apply(block)
            .build()

    fun command(name: String, value: CommandBase, block: ShuffleboardComplexWidgetBuilder.() -> Unit) =
        ShuffleboardComplexWidgetBuilder(tab.add(name, value).withWidget(BuiltInWidgets.kCommand))
            .apply(block)
            .build()

    fun pidCommand(name: String, value: PIDCommand, block: ShuffleboardComplexWidgetBuilder.() -> Unit) =
        ShuffleboardComplexWidgetBuilder(tab.add(name, value).withWidget(BuiltInWidgets.kPIDCommand))
            .apply(block)
            .build()

    fun pidController(name: String, value: PIDController, block: ShuffleboardComplexWidgetBuilder.() -> Unit) =
        ShuffleboardComplexWidgetBuilder(tab.add(name, value).withWidget(BuiltInWidgets.kPIDController))
            .apply(block)
            .build()

    fun accelerometer(name: String, value: GyroBase, block: ShuffleboardComplexWidgetBuilder.() -> Unit) =
        ShuffleboardComplexWidgetBuilder(tab.add(name, value).withWidget(BuiltInWidgets.kAccelerometer))
            .apply(block)
            .build()

    fun gyro(name: String, value: GyroBase, block: ShuffleboardComplexWidgetBuilder.() -> Unit) =
        ShuffleboardComplexWidgetBuilder(tab.add(name, value).withWidget(BuiltInWidgets.kGyro))
            .apply(block)
            .build()

    fun relay(name: String, value: GyroBase, block: ShuffleboardComplexWidgetBuilder.() -> Unit) =
        ShuffleboardComplexWidgetBuilder(tab.add(name, value).withWidget(BuiltInWidgets.kRelay))
            .apply(block)
            .build()

    fun sendable(name: String, value: Sendable, block: ShuffleboardComplexWidgetBuilder.() -> Unit) =
        ShuffleboardComplexWidgetBuilder(tab.add(name, value))
            .apply(block)
            .build()

    fun layout(name: String, layoutType: LayoutType, block: ShuffleboardLayoutBuilder.() -> Unit) =
        ShuffleboardLayoutBuilder(tab.getLayout(name, layoutType))
            .apply(block)
            .build()

    fun list(name: String, block: ShuffleboardLayoutBuilder.() -> Unit) =
        layout(name, BuiltInLayouts.kList, block)

    fun grid(name: String, block: ShuffleboardLayoutBuilder.() -> Unit) = layout(name, BuiltInLayouts.kGrid, block)
}

class ShuffleboardWidgetBuilder<T>(private val widget: SuppliedValueWidget<T>) {
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

class ShuffleboardComplexWidgetBuilder(private val widget: ComplexWidget) {
    fun position(column: Int, row: Int) {
        widget.withPosition(column, row)
    }

    fun size(width: Int, height: Int) {
        widget.withSize(width, height)
    }

    fun properties(vararg props: Pair<String, Any>) {
        widget.withProperties(mapOf(*props))
    }

    fun build(): ComplexWidget {
        return widget
    }
}

class FalconShuffleboardLayout(val layout: ShuffleboardLayout)

class ShuffleboardLayoutBuilder(private val layout: ShuffleboardLayout) {
    fun position(column: Int, row: Int) {
        layout.withPosition(column, row)
    }

    fun size(width: Int, height: Int) {
        layout.withSize(width, height)
    }

    fun properties(vararg props: Pair<String, Any>) {
        layout.withProperties(mapOf(*props))
    }

    fun sendable(name: String, sendable: Sendable): ComplexWidget =
        layout.add(name, sendable)

    fun number(name: String, value: Source<Double>): SuppliedValueWidget<Double> =
        layout.addNumber(name, value)

    fun double(name: String, value: Source<Double>) = number(name, value)

    fun booleanProperty(name: String, value: Source<Boolean>): SuppliedValueWidget<Boolean> =
        layout.addBoolean(name, value)

    fun booleanArrayProperty(name: String, value: Source<BooleanArray>): SuppliedValueWidget<BooleanArray> =
        layout.addBooleanArray(name, value)

    fun numberArrayProperty(name: String, value: Source<DoubleArray>): SuppliedValueWidget<DoubleArray> =
        layout.addDoubleArray(name, value)

    fun stringProperty(name: String, value: Source<String>): SuppliedValueWidget<String> =
        layout.addString(name, value)

    fun stringArrayProperty(name: String, value: Source<Array<out String>>): SuppliedValueWidget<Array<String>> =
        layout.addStringArray(name, value)

    fun build() = FalconShuffleboardLayout(layout)
}
