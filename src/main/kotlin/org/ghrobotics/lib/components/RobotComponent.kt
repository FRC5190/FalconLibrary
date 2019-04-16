package org.ghrobotics.lib.components

import org.ghrobotics.lib.mathematics.threedim.geometry.Transform

abstract class RobotComponent {

    var transform = Transform()
        protected set

    var parent: RobotComponent? = null
        private set

    private val _children = mutableListOf<RobotComponent>()

    protected fun addComponent(component: RobotComponent) {
        if (component.parent != null) throw IllegalStateException("Component already has been added to another parent")
        component.parent = this
        _children += component
    }

    open fun update() {
        _children.forEach(RobotComponent::update)
    }

}