package org.ghrobotics.lib.components

import org.ghrobotics.lib.mathematics.threedim.geometry.Transform

abstract class RobotComponent {

    var localTransform = Transform()
        protected set

    var worldTransform = Transform()
        private set

    var parent: RobotComponent? = null
        private set

    private val _children = mutableListOf<RobotComponent>()

    protected fun addComponent(component: RobotComponent) {
        if (component.parent != null) throw IllegalStateException("Component already has been added to another parent")
        component.parent = this
        _children += component
    }

    open fun update() {
        val parent = this.parent
        if(parent != null) {
            worldTransform = parent.worldTransform + localTransform
        }
        _children.forEach(RobotComponent::update)
    }

}