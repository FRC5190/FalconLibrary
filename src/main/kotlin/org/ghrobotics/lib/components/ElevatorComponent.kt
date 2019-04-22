package org.ghrobotics.lib.components

import org.ghrobotics.lib.mathematics.threedim.geometry.Pose3d
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d
import org.ghrobotics.lib.mathematics.units.Length

abstract class ElevatorComponent(
    private val elevatorZero: Translation3d
) : MotorComponent<Length>() {

    protected abstract val elevatorKg: Double

    override fun updateState() {
        
        arbitraryFeedForward = elevatorKg

        super.updateState()
    }

    override fun useState() {

        localTransform = Pose3d(
            translation = Translation3d(
                elevatorZero.x,
                elevatorZero.y,
                elevatorZero.z + motor.encoder.position
            )
        )

        super.useState()
    }

}