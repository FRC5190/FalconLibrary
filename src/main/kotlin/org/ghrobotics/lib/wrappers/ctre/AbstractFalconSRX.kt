package org.ghrobotics.lib.wrappers.ctre

import com.ctre.phoenix.motorcontrol.*
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derivedunits.Acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.volt
import org.ghrobotics.lib.mathematics.units.nativeunits.nativeUnits
import org.ghrobotics.lib.wrappers.FalconMotor
import kotlin.properties.Delegates.observable

abstract class AbstractFalconSRX<T : SIValue<T>>(
    id: Int,
    timeout: Time
) : TalonSRX(id), FalconMotor<T> {

    // CTRE TalonSRX

    protected val timeoutInt = timeout.millisecond.toInt()

    var kP by observable(0.0) { _, _, newValue -> config_kP(0, newValue, timeoutInt) }
    var kI by observable(0.0) { _, _, newValue -> config_kI(0, newValue, timeoutInt) }
    var kD by observable(0.0) { _, _, newValue -> config_kD(0, newValue, timeoutInt) }
    var kF by observable(0.0) { _, _, newValue -> config_kF(0, newValue, timeoutInt) }
    var encoderPhase by observable(false) { _, _, newValue -> setSensorPhase(newValue) }

    var overrideLimitSwitchesEnable by observable(false) { _, _, newValue ->
        overrideLimitSwitchesEnable(newValue)
    }

    var softLimitForwardEnabled by observable(false) { _, _, newValue ->
        configForwardSoftLimitEnable(newValue, timeoutInt)
    }
    var softLimitReverseEnabled by observable(false) { _, _, newValue ->
        configReverseSoftLimitEnable(newValue, timeoutInt)
    }
    var softLimitForward by observable(0.nativeUnits) { _, _, newValue ->
        configForwardSoftLimitThreshold(newValue.value.toInt(), timeoutInt)
    }
    var softLimitReverse by observable(0.nativeUnits) { _, _, newValue ->
        configReverseSoftLimitThreshold(newValue.value.toInt(), timeoutInt)
    }

    var brakeMode by observable(NeutralMode.Coast) { _, _, newValue ->
        setNeutralMode(newValue)
    }
    abstract var allowedClosedLoopError: T

    var nominalForwardOutput by observable(0.0) { _, _, newValue ->
        configNominalOutputForward(newValue, timeoutInt)
    }
    var nominalReverseOutput by observable(0.0) { _, _, newValue ->
        configNominalOutputReverse(newValue, timeoutInt)
    }

    var peakForwardOutput by observable(1.0) { _, _, newValue ->
        configPeakOutputForward(newValue, timeoutInt)
    }
    var peakReverseOutput by observable(-1.0) { _, _, newValue ->
        configPeakOutputReverse(newValue, timeoutInt)
    }

    var openLoopRamp by observable(0.second) { _, _, newValue ->
        configOpenloopRamp(newValue.second, timeoutInt)
    }
    val closedLoopRamp by observable(0.second) { _, _, newValue ->
        configClosedloopRamp(newValue.second, timeoutInt)
    }

    abstract var motionCruiseVelocity: Velocity<T>
    abstract var motionAcceleration: Acceleration<T>

    var feedbackSensor by observable(FeedbackDevice.QuadEncoder) { _, _, newValue ->
        configSelectedFeedbackSensor(newValue, 0, timeoutInt)
    } // no feedback sensor is not an option anymore

    var peakCurrentLimit by observable(0.amp) { _, _, newValue ->
        configPeakCurrentLimit(newValue.amp.toInt(), timeoutInt)
    }
    var peakCurrentLimitDuration by observable(0.millisecond) { _, _, newValue ->
        configPeakCurrentDuration(newValue.millisecond.toInt(), timeoutInt)
    }
    var continuousCurrentLimit by observable(0.amp) { _, _, newValue ->
        configContinuousCurrentLimit(newValue.amp.toInt(), timeoutInt)
    }
    var currentLimitingEnabled by observable(false) { _, _, newValue ->
        enableCurrentLimit(newValue)
    }

    var voltageCompensationSaturation by observable(12.volt) { _, _, newValue ->
        configVoltageCompSaturation(newValue.value, timeoutInt)
    }
    var voltageCompensationEnabled by observable(false) { _, _, newValue ->
        enableVoltageCompensation(newValue)
    }

    var forwardLimitSwitchSource by observable(LimitSwitchSource.Deactivated to LimitSwitchNormal.NormallyOpen) { _, _, newValue ->
        configForwardLimitSwitchSource(newValue.first, newValue.second)
    }

    var reverseLimitSwitchSource by observable(LimitSwitchSource.Deactivated to LimitSwitchNormal.NormallyOpen) { _, _, newValue ->
        configReverseLimitSwitchSource(newValue.first, newValue.second)
    }

    var clearPositionOnForwardLimitSwitch by observable(false) { _, _, newValue ->
        configClearPositionOnLimitF(newValue, timeoutInt)
    }

    var clearPositionOnReverseLimitSwitch by observable(false) { _, _, newValue ->
        configClearPositionOnLimitR(newValue, timeoutInt)
    }


    abstract var sensorPosition: T
    abstract val sensorVelocity: Velocity<T>

    abstract val activeTrajectoryPosition: T
    abstract val activeTrajectoryVelocity: Velocity<T>

    abstract fun set(controlMode: ControlMode, length: T)

    abstract fun set(controlMode: ControlMode, velocity: Velocity<T>)

    abstract fun set(
        controlMode: ControlMode,
        velocity: Velocity<T>,
        demandType: DemandType,
        outputPercent: Double
    )

    abstract fun set(
        controlMode: ControlMode,
        length: T,
        demandType: DemandType,
        outputPercent: Double
    )

    // Falcon Motor

    override var percentOutput: Double
        get() = motorOutputPercent
        set(value) {
            set(ControlMode.PercentOutput, value)
        }

    override val voltageOutput get() = motorOutputVoltage

    abstract override var velocity: Double

    abstract override fun setVelocityAndArbitraryFeedForward(velocity: Double, arbitraryFeedForward: Double)

    init {
        // Clear all redundant settings.
        @Suppress("LeakingThis")
        configFactoryDefault()
    }
}