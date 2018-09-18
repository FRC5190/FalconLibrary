package org.ghrobotics

import com.badlogic.gdx.math.Vector2

object RobotTracker {

    val robotHistory = mutableListOf<RobotHistorySegment>()
    val pathHistory = mutableListOf<RobotHistorySegment>()

    var currentRobotSegment = RobotHistorySegment(Vector2(), 0.0)
        private set

    fun update() {
        currentRobotSegment = RobotHistorySegment(
                Vector2(Communications.robotX.toFloat() / 3f, Constants.FIELD_WIDTH - Communications.effectiveRobotY.toFloat() / 3f),
                Communications.effectiveRobotHeading
        )
        val currentPathSegment = RobotHistorySegment(
                Vector2(Communications.pathX.toFloat() / 3f, Constants.FIELD_WIDTH - Communications.effectivePathY.toFloat() / 3f),
                Communications.effectivePathHeading
        )
        robotHistory.addIfDifferent(currentRobotSegment)
        pathHistory.addIfDifferent(currentPathSegment)
    }

    private fun <T> MutableList<T>.addIfDifferent(value: T) {
        val lastSegment = lastOrNull()
        if (lastSegment != value) add(value)
    }

    fun reset() {
        robotHistory.clear()
        pathHistory.clear()
    }

}

data class RobotHistorySegment(val location: Vector2, val angle: Double)