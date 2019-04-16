package org.ghrobotics.lib.components

import org.ghrobotics.lib.mathematics.threedim.geometry.Vector3

/**
 * Standard cascade elevator with both stages moving at the same time
 */
abstract class CascadeElevatorComponent(
    elevatorZero: Vector3,
    final override val elevatorKg: Double
) : ElevatorComponent(elevatorZero)

/**
 * Cascade Elevator with a spring attached to delay the second stage when going up (increases max height of elevator)
 */
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