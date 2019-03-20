package org.ghrobotics.lib.wrappers

import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.SIValue

typealias LinearFalconMotor = FalconMotor<Length>

interface FalconMotor<T : SIValue<T>> {

    /**
     * Setting this value will command the motor to run at the specified output percentage
     * Getting this value will return the current output percentage of the motor
     */
    var percentOutput: Double

    /**
     * Getting this value will return the current output voltage of the motor
     */
    val voltageOutput: Double

    /**
     * Setting this value will command the motor to run at the specified velocity
     * Getting this value will return the current velocity of the motor
     */
    var velocity: Double

    fun setVelocityAndArbitraryFeedForward(
        velocity: Double,
        arbitraryFeedForward: Double
    )

}

