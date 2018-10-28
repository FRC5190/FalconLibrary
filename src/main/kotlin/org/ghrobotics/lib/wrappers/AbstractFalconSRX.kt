package org.ghrobotics.lib.wrappers

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derivedunits.volt
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac11
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac12
import org.ghrobotics.lib.mathematics.units.nativeunits.STU
import kotlin.properties.Delegates

abstract class AbstractFalconSRX<T : SIValue<T>>(
        id: Int,
        timeout: Time
) : TalonSRX(id) {
    protected val timeoutInt = timeout.millisecond.asInt

    var kP by Delegates.observable(0.0) { _, _, newValue -> config_kP(0, newValue, timeoutInt) }
    var kI by Delegates.observable(0.0) { _, _, newValue -> config_kI(0, newValue, timeoutInt) }
    var kD by Delegates.observable(0.0) { _, _, newValue -> config_kD(0, newValue, timeoutInt) }
    var kF by Delegates.observable(0.0) { _, _, newValue -> config_kF(0, newValue, timeoutInt) }
    var encoderPhase by Delegates.observable(false) { _, _, newValue -> setSensorPhase(newValue) }

    var overrideLimitSwitchesEnable by Delegates.observable(false) { _, _, newValue ->
        overrideLimitSwitchesEnable(
                newValue
        )
    }

    var softLimitForwardEnabled by Delegates.observable(false) { _, _, newValue ->
        configForwardSoftLimitEnable(
                newValue,
                timeoutInt
        )
    }
    var softLimitReverseEnabled by Delegates.observable(false) { _, _, newValue ->
        configReverseSoftLimitEnable(
                newValue,
                timeoutInt
        )
    }
    var softLimitForward by Delegates.observable(0.STU) { _, _, newValue ->
        configForwardSoftLimitThreshold(
                newValue.asInt,
                timeoutInt
        )
    }
    var softLimitReverse by Delegates.observable(0.STU) { _, _, newValue ->
        configReverseSoftLimitThreshold(
                newValue.asInt,
                timeoutInt
        )
    }

    var brakeMode by Delegates.observable(NeutralMode.Coast) { _, _, newValue ->
        setNeutralMode(
                newValue
        )
    }
    abstract var allowedClosedLoopError: T

    var nominalForwardOutput by Delegates.observable(0.0) { _, _, newValue ->
        configNominalOutputForward(
                newValue,
                timeoutInt
        )
    }
    var nominalReverseOutput by Delegates.observable(0.0) { _, _, newValue ->
        configNominalOutputReverse(
                newValue,
                timeoutInt
        )
    }

    var peakForwardOutput by Delegates.observable(1.0) { _, _, newValue ->
        configPeakOutputForward(
                newValue,
                timeoutInt
        )
    }
    var peakReverseOutput by Delegates.observable(-1.0) { _, _, newValue ->
        configPeakOutputReverse(
                newValue,
                timeoutInt
        )
    }

    var openLoopRamp by Delegates.observable(0.second) { _, _, newValue ->
        configOpenloopRamp(
                newValue.second.asDouble,
                timeoutInt
        )
    }
    val closedLoopRamp by Delegates.observable(0.second) { _, _, newValue ->
        configClosedloopRamp(
                newValue.second.asDouble,
                timeoutInt
        )
    }

    abstract var motionCruiseVelocity: SIFrac11<T, Time>
    abstract var motionAcceleration: SIFrac12<T, Time, Time>

    var feedbackSensor by Delegates.observable(FeedbackDevice.None) { _, _, newValue ->
        configSelectedFeedbackSensor(
                newValue,
                0,
                timeoutInt
        )
    }

    var peakCurrentLimit by Delegates.observable(0.amp) { _, _, newValue ->
        configPeakCurrentLimit(
                newValue.amp.asInt,
                timeoutInt
        )
    }
    var peakCurrentLimitDuration by Delegates.observable(0.millisecond) { _, _, newValue ->
        configPeakCurrentDuration(
                newValue.millisecond.asInt,
                timeoutInt
        )
    }
    var continuousCurrentLimit by Delegates.observable(0.amp) { _, _, newValue ->
        configContinuousCurrentLimit(
                newValue.amp.asInt,
                timeoutInt
        )
    }
    var currentLimitingEnabled by Delegates.observable(false) { _, _, newValue ->
        enableCurrentLimit(
                newValue
        )
    }

    var voltageCompensationSaturation by Delegates.observable(12.volt) { _, _, newValue ->
        configVoltageCompSaturation(
                newValue.asMetric.asDouble,
                timeoutInt
        )
    }
    var voltageCompensationEnabled by Delegates.observable(false) { _, _, newValue ->
        enableVoltageCompensation(
                newValue
        )
    }

    abstract var sensorPosition: T
    abstract val sensorVelocity: SIFrac11<T, Time>

    abstract fun set(controlMode: ControlMode, length: T)

    abstract fun set(controlMode: ControlMode, velocity: SIFrac11<T, Time>)

    abstract fun set(
            controlMode: ControlMode,
            velocity: SIFrac11<T, Time>,
            demandType: DemandType,
            outputPercent: Double
    )
}