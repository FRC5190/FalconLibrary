package org.ghrobotics.lib.components

import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.mathematics.threedim.geometry.Transform
import org.ghrobotics.lib.utils.DeltaTime

abstract class RobotComponent {

    // Local Transforms

    var localTransform = Transform()
        protected set

    var localVelocityTransform = Transform()
        protected set

    // World Transforms

    var worldTransform = Transform()
        private set

    var worldVelocityTransform = Transform()
        private set

    var worldAccelerationTransform = Transform()
        private set

    var parent: RobotComponent? = null
        private set

    private val _children = mutableListOf<RobotComponent>()

    private val loopDeltaTime = DeltaTime()

    protected fun addComponent(component: RobotComponent) {
        if (component.parent != null) throw IllegalStateException("Component already has been added to another parent")
        component.parent = this
        _children += component
    }

    open fun updateState() {

        val dt = loopDeltaTime.updateTime(Timer.getFPGATimestamp())

        val lastWorldVelocityTransform = this.worldVelocityTransform

        val parent = this.parent
        if (parent != null) {
            worldTransform = parent.worldTransform + localTransform
            worldVelocityTransform = parent.worldVelocityTransform + localVelocityTransform
        } else {
            worldTransform = localTransform
            worldVelocityTransform = localVelocityTransform
        }

        if (dt > 0.0) {
            worldAccelerationTransform = (worldVelocityTransform - lastWorldVelocityTransform) / dt
        }

        _children.forEach(RobotComponent::updateState)
    }

    open fun useState() {
        _children.forEach(RobotComponent::useState)
    }

}