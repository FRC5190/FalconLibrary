package org.ghrobotics.lib.wrappers.rev

import com.revrobotics.CANEncoder
import com.revrobotics.CANPIDController
import com.revrobotics.CANSparkMax
import com.revrobotics.ControlType
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derivedunits.*
import org.ghrobotics.lib.wrappers.FalconMotor
import kotlin.properties.Delegates.observable


/**
 * REV Robotics Spark Max - Brushless mode
 */

abstract class BrushlessSparkMax<T : SIValue<T>> (
        id: Int
) : CANSparkMax(id, MotorType.kBrushless), FalconMotor<T> {

    // The PID Controller for this spark max
    val pidController : CANPIDController by lazy {
        getPIDController()
    }

    // The hall effect encoder for this spark max
    val canEncoder : CANEncoder by lazy {
        encoder
    }

    // PID Parameters
    var kP by observable(0.0) {_, _, newVal -> pidController.setP(newVal, 0)}
    var kI by observable(0.0) {_, _, newVal -> pidController.setI(newVal, 0)}
    var kD by observable(0.0) {_, _, newVal -> pidController.setD(newVal, 0)}
    var kF by observable(0.0) {_, _, newVal -> pidController.setFF(newVal, 0)}

    // Kinda hacky, since SparkMax's don't seem to support disabling current limits other
    // than setting the limit to zero.
    var currentLimitingEnabled by observable(false) {_, _, newVal ->
        setSmartCurrentLimit(if(newVal) currentLimit else 0.amp)}

    // A Static current limit regardless of RPM
    var currentLimit by observable(0.0.amp) {_, _, newVal -> setSmartCurrentLimit(newVal)}

    // Set a smart current limit that changes based on RPM
    fun setSmartCurrentLimit(stallCurrent : ElectricCurrent,
                        continuousCurrent : ElectricCurrent? = null,
                        speedLimit : AngularVelocity? = null) {
        when {
            continuousCurrent == null -> setSmartCurrentLimit(stallCurrent.amp.toInt())
            speedLimit == null -> setSmartCurrentLimit(stallCurrent.amp.toInt(), continuousCurrent.amp.toInt())
            else -> setSmartCurrentLimit(stallCurrent.amp.toInt(), continuousCurrent.amp.toInt(),
                    speedLimit.times(1.minute).degree.div(360).toInt())
        }
    }

    var brakeMode by observable(IdleMode.kCoast) {_, _, newVal -> setIdleMode(newVal)}

    abstract var sensorPosition: T
    abstract val sensorVelocity: Velocity<T>

    abstract val activeTrajectoryPosition: T
    abstract val activeTrajectoryVelocity: Velocity<T>

    abstract fun set(controlType: ControlType, length: T)

    abstract fun set(controlType: ControlType, velocity: Velocity<T>)

    abstract fun set(
            controlType: ControlType,
            velocity: Velocity<T>,
            outputPercent: Double
    )

    abstract fun set(
            controlType: ControlType,
            length: T,
            outputPercent: Double
    )

    // Falcon Motor

    override var percentOutput: Double
        get() = appliedOutput
        set(value) {
            set(value)
        }

    // Again hacky since spark max's don't support getting output voltage
    override val voltageOutput: Volt
        get() = percentOutput.times(busVoltage).volt


    override var velocity: Velocity<T>
        get() = sensorVelocity
        set(value) {
            set(ControlType.kVelocity, value)
        }

    override fun setVelocityAndArbitraryFeedForward(velocity: Velocity<T>, arbitraryFeedForward: Double) {
        set(ControlType.kVelocity, velocity, arbitraryFeedForward)
    }

    init {
        // Clear all redundant settings.
        @Suppress("LeakingThis")
        restoreFactoryDefaults()
    }
}