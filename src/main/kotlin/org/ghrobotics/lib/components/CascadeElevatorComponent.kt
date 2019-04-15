package org.ghrobotics.lib.components

import org.ghrobotics.lib.mathematics.threedim.geometry.Vector3
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.motors.FalconMotor

abstract class CascadeElevatorComponent(
    elevatorZero: Vector3,
    override val elevatorKg: Double
) : ElevatorComponent(elevatorZero)


abstract class SpringCascadeElevatorComponent(
    elevatorZero: Vector3,
    private val elevatorSwitchHeight: Double,
    private val elevatorAboveSwitchKg: Double,
    private val elevatorBelowSwitchKg: Double
) : ElevatorComponent(elevatorZero) {

    final override var elevatorKg: Double = 0.0
        private set

    override fun update() {
        elevatorKg = if (position >= elevatorSwitchHeight) {
            elevatorAboveSwitchKg
        } else {
            elevatorBelowSwitchKg
        }
        super.update()
    }
}