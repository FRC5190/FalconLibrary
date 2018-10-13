/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.ghrobotics.lib.mathematics.units.derivedunits.Acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.feet
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitSettings
import org.ghrobotics.lib.mathematics.units.nativeunits.STU
import org.ghrobotics.lib.mathematics.units.nativeunits.STUPer100ms
import org.ghrobotics.lib.mathematics.units.old.Amps
import org.ghrobotics.lib.mathematics.units.old.Current
import org.ghrobotics.lib.mathematics.units.old.Voltage
import org.ghrobotics.lib.mathematics.units.old.Volts
import org.ghrobotics.lib.mathematics.units.second
import kotlin.reflect.KProperty

class FalconSRX(
    id: Int,
    val nativeUnitSettings: NativeUnitSettings,
    private val timeoutMs: Int = 10
) : TalonSRX(id) {

    private fun <T> propInit(initValue: T, set: FalconSRX.(T) -> Unit): FalconSRXProp<T> {
        var value = initValue
        return prop({
            value = it
            set(this, it)
        }) { value }
    }

    private fun <T> prop(set: FalconSRX.(T) -> Unit, get: FalconSRX.() -> T) = FalconSRXProp(set, get)

    private class FalconSRXProp<T>(
        private val set: FalconSRX.(T) -> Unit,
        private val get: FalconSRX.() -> T
    ) {
        operator fun setValue(thisRef: FalconSRX, property: KProperty<*>, value: T) {
            synchronized(this) {
                set(thisRef, value)
            }
        }

        operator fun getValue(thisRef: FalconSRX, property: KProperty<*>) = synchronized(this) { get(thisRef) }
    }

    var kP by propInit(0.0) { config_kP(0, it, timeoutMs) }
    var kI by propInit(0.0) { config_kI(0, it, timeoutMs) }
    var kD by propInit(0.0) { config_kD(0, it, timeoutMs) }
    var kF by propInit(0.0) { config_kF(0, it, timeoutMs) }
    var encoderPhase by propInit(false) { setSensorPhase(it) }

    var overrideLimitSwitchesEnable by propInit(false) { overrideLimitSwitchesEnable(it) }
    var softLimitFwd by propInit(0.feet) {
        configForwardSoftLimitThreshold(
            it.STU(nativeUnitSettings).asInt,
            timeoutMs
        )
    }
    var softLimitRev by propInit(0.feet) {
        configReverseSoftLimitThreshold(
            it.STU(nativeUnitSettings).asInt,
            timeoutMs
        )
    }
    var softLimitFwdEnabled by propInit(false) { configForwardSoftLimitEnable(it, timeoutMs) }
    var softLimitRevEnabled by propInit(false) { configReverseSoftLimitEnable(it, timeoutMs) }

    var brakeMode by propInit(NeutralMode.Coast) { setNeutralMode(it) }
    var closedLoopTolerance by propInit(0.feet) {
        configAllowableClosedloopError(
            0,
            it.STU(nativeUnitSettings).asInt,
            timeoutMs
        )
    }

    var nominalFwdOutput by propInit(0.0) { configNominalOutputForward(it, timeoutMs) }
    var nominalRevOutput by propInit(0.0) { configNominalOutputReverse(it, timeoutMs) }

    var peakFwdOutput by propInit(1.0) { configPeakOutputForward(it, timeoutMs) }
    var peakRevOutput by propInit(-1.0) { configPeakOutputReverse(it, timeoutMs) }

    var openLoopRamp by propInit(0.second) { configOpenloopRamp(it.second.asDouble, timeoutMs) }
    var closedLoopRamp by propInit(0.second) { configClosedloopRamp(it.second.asDouble, timeoutMs) }

    var motionCruiseVelocity by propInit(Velocity.ZERO) {
        configMotionCruiseVelocity(
            it.STU(nativeUnitSettings).STUPer100ms.asInt,
            timeoutMs
        )
    }
    var motionAcceleration by propInit(Acceleration.ZERO) {
        configMotionAcceleration(
            it.STU(nativeUnitSettings).STUPer100msPerSecond.asInt,
            timeoutMs
        )
    }

    var feedbackSensor by propInit(FeedbackDevice.None) { configSelectedFeedbackSensor(it, 0, timeoutMs) }
    var peakCurrentLimit by propInit<Current>(
        Amps(0)
    ) { configPeakCurrentLimit(it.amps, timeoutMs) }

    var peakCurrentLimitDuration by propInit(0.millisecond) {
        configPeakCurrentDuration(
            it.millisecond.asInt,
            timeoutMs
        )
    }
    var continuousCurrentLimit by propInit<Current>(
        Amps(0)
    ) { configContinuousCurrentLimit(it.amps, timeoutMs) }
    var currentLimitingEnabled by propInit(false) { enableCurrentLimit(it) }

    var voltageCompensationSaturation by propInit<Voltage>(
        Volts(12.0)
    ) { configVoltageCompSaturation(it.volts, timeoutMs) }
    var voltageCompensationEnabled by propInit(false) { enableVoltageCompensation(it) }

    var sensorPosition by prop({
        setSelectedSensorPosition(
            it.STU(nativeUnitSettings).asInt,
            0,
            timeoutMs
        )
    }) { getSelectedSensorPosition(0).STU(nativeUnitSettings) }
    val sensorVelocity
        get() = getSelectedSensorVelocity(0).STUPer100ms(nativeUnitSettings)

    init {
        //        kP = 0.0; kI = 0.0; kD = 0.0; kF = 0.0
        //        encoderPhase = false; overrideLimitSwitchesEnable = false
        //        softLimitFwd = NativeUnits(0); softLimitFwdEnabled = false
        //        softLimitRev = NativeUnits(0); softLimitRevEnabled = false
        //        openLoopRamp = Seconds(0.0); closedLoopRamp = Seconds(0.0)
        //        motionCruiseVelocity = NativeUnitsPer100Ms(0); motionAcceleration = 0
        //        feedbackSensor = FeedbackDevice.None
        //        peakCurrentLimit = Amps(0); continousCurrentLimit = Amps(0)
        //        peakCurrentLimitDuration = Seconds(0.0); currentLimitingEnabled = false
        //        voltageCompensationSaturation = Volts(12.0); voltageCompensationEnabled = false
    }

}


