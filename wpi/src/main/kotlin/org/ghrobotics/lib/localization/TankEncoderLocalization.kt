package org.ghrobotics.lib.localization

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.utils.Source

class TankEncoderLocalization(
    robotHeading: Source<Rotation2d>,
    val leftEncoder: Source<Double>,
    val rightEncoder: Source<Double>,
    localizationBuffer: TimeInterpolatableBuffer<Pose2d> = TimeInterpolatableBuffer()
) : Localization(robotHeading, localizationBuffer) {

    private var prevLeftEncoder = 0.0
    private var prevRightEncoder = 0.0

    override fun resetInternal(newPosition: Pose2d) {
        super.resetInternal(newPosition)
        prevLeftEncoder = leftEncoder()
        prevRightEncoder = rightEncoder()
    }

    override fun update(deltaHeading: Rotation2d): Pose2d {
        val newLeftEncoder = leftEncoder()
        val newRightEncoder = rightEncoder()

        val deltaLeft = newLeftEncoder - prevLeftEncoder
        val deltaRight = newRightEncoder - prevRightEncoder

        this.prevLeftEncoder = newLeftEncoder
        this.prevRightEncoder = newRightEncoder

        return forwardKinematics(deltaLeft, deltaRight, deltaHeading).asPose
    }

    /**
     * Return a twist that represents the robot's motion from the left delta, the right delta, and the rotation delta.
     */
    private fun forwardKinematics(leftDelta: Double, rightDelta: Double, rotationDelta: Rotation2d): Twist2d {
        val dx = (leftDelta + rightDelta) / 2.0
        return Twist2d(dx, 0.0, rotationDelta)
    }
}