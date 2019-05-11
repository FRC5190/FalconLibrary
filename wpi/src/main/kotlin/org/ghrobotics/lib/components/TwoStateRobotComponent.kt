package org.ghrobotics.lib.components

import edu.wpi.first.wpilibj.DoubleSolenoid
import org.ghrobotics.lib.mathematics.threedim.geometry.Pose3d
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d

@Suppress("FunctionName")
fun TwoStateRobotComponent(
    solenoid: DoubleSolenoid,
    trueStateDisplacement: Translation3d,
    falseStateDisplacement: Translation3d
) = TwoStateRobotComponent(
    solenoid.get() == DoubleSolenoid.Value.kForward,
    trueStateDisplacement,
    falseStateDisplacement
) { solenoid.set(if (it) DoubleSolenoid.Value.kForward else DoubleSolenoid.Value.kReverse) }

class TwoStateRobotComponent(
    startState: Boolean,
    val trueStateDisplacement: Translation3d,
    val falseStateDisplacement: Translation3d,
    private val setState: (newState: Boolean) -> Unit
) : RobotComponent() {

    var wantedState = startState
    var currentState = !startState
        private set

    override fun useState() {
        
        if (currentState != wantedState) {
            currentState = wantedState
            setState(currentState)
            localTransform = Pose3d(
                translation = if (currentState) {
                    trueStateDisplacement
                } else {
                    falseStateDisplacement
                }
            )
        }

        super.useState()
    }
}