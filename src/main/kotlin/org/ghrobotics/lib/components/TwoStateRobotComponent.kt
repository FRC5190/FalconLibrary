package org.ghrobotics.lib.components

import edu.wpi.first.wpilibj.DoubleSolenoid
import org.ghrobotics.lib.mathematics.threedim.geometry.Pose3d
import org.ghrobotics.lib.mathematics.threedim.geometry.Vector3

@Suppress("FunctionName")
fun TwoStateRobotComponent(
    solenoid: DoubleSolenoid,
    trueStateDisplacement: Vector3,
    falseStateDisplacement: Vector3
) = TwoStateRobotComponent(
    solenoid.get() == DoubleSolenoid.Value.kForward,
    trueStateDisplacement,
    falseStateDisplacement
) { solenoid.set(if (it) DoubleSolenoid.Value.kForward else DoubleSolenoid.Value.kReverse) }

class TwoStateRobotComponent(
    startState: Boolean,
    val trueStateDisplacement: Vector3,
    val falseStateDisplacement: Vector3,
    private val setState: (newState: Boolean) -> Unit
) : RobotComponent() {

    var wantedState = startState
    var currentState = !startState
        private set

    override fun update() {
        if (currentState != wantedState) {
            currentState = wantedState
            setState(currentState)
            transform = Pose3d(
                translation = if (currentState) {
                    trueStateDisplacement
                } else {
                    falseStateDisplacement
                }
            )
        }
        super.update()
    }
}