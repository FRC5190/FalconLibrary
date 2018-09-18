package org.ghrobotics.objects

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.utils.Disposable

abstract class WorldObject(model: Model) : Disposable {

    val modelInstance = ModelInstance(model)

    abstract val collisionObject: btCollisionObject
    open val collisionObjectOffset = Vector3()

    fun proceedToTransform(position: Vector3, quaternion: Quaternion) {
        val collisionObject = this.collisionObject

        val matrix = Matrix4().set(position.cpy().sub(collisionObjectOffset), quaternion)

        if(collisionObject is btRigidBody) {
            collisionObject.proceedToTransform(matrix)
        }else{
            collisionObject.worldTransform = matrix
        }
    }

    open fun addToWorld(world: btDiscreteDynamicsWorld) {
        val collisionObject = this.collisionObject

        if (collisionObject is btRigidBody) world.addRigidBody(collisionObject)
        else world.addCollisionObject(collisionObject)
    }

    open fun removeFromWorld(world: btDiscreteDynamicsWorld) {
        val collisionObject = this.collisionObject

        if (collisionObject is btRigidBody) world.removeRigidBody(collisionObject)
        else world.removeCollisionObject(collisionObject)
    }

}