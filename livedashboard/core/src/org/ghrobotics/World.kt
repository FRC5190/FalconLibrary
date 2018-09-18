package org.ghrobotics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import org.ghrobotics.objects.WorldObject

class World {

    val dynamicsWorld: btDiscreteDynamicsWorld

    private val objects = mutableListOf<WorldObject>()


    init {
        val collisionConfig = btDefaultCollisionConfiguration()
        val collisionDispatcher = btCollisionDispatcher(collisionConfig)

        val dbvtBroadphase = btDbvtBroadphase()
        dbvtBroadphase.overlappingPairCache.setInternalGhostPairCallback(btGhostPairCallback())

        val constraintSolver = btSequentialImpulseConstraintSolver()

        dynamicsWorld = btDiscreteDynamicsWorld(collisionDispatcher, dbvtBroadphase, constraintSolver, collisionConfig)
        dynamicsWorld.gravity = Vector3(0f, -9.8f, 0f)
    }

    fun addToWorld(worldObject: WorldObject) {
        worldObject.addToWorld(dynamicsWorld)
        objects += worldObject
    }

    fun removeFromWorld(worldObject: WorldObject) {
        worldObject.removeFromWorld(dynamicsWorld)
        objects -= worldObject
    }

    fun update() {
        dynamicsWorld.stepSimulation(Gdx.graphics.deltaTime, 5, 1f / 60f)

        objects.forEach {
            it.collisionObject.getWorldTransform(it.modelInstance.transform)
            //it.modelInstance.transform.translate(it.collisionObjectOffset)
        }
    }

}