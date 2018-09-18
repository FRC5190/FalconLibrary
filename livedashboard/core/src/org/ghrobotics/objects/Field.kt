package org.ghrobotics.objects

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody

class Field(model: Model) : WorldObject(model) {

    override val collisionObject: btRigidBody
    override val collisionObjectOffset: Vector3

    init {
        val boundingBox = modelInstance.calculateBoundingBox(BoundingBox())
        collisionObject = btRigidBody(btRigidBody.btRigidBodyConstructionInfo(0f, null, btBoxShape(Vector3(boundingBox.width / 2f, boundingBox.height / 2f, boundingBox.depth / 2f))))

        collisionObjectOffset = Vector3(0f, -boundingBox.height, 0f)
    }

    override fun dispose() {
        collisionObject.dispose()
    }

}