/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package frc.team5190.lib.wrappers

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import frc.team5190.lib.math.units.*
import kotlin.reflect.KProperty

class FalconSRX(id: Int, private val timeoutMs: Int = 10) : TalonSRX(id) {

    private fun <T> propInit(initValue: T, set: FalconSRX.(T) -> Unit): FalconSRXProp<T> {
        var value = initValue
        return prop({
            value = it
            set(this, it)
        }) { value }
    }

    private fun <T> prop(set: FalconSRX.(T) -> Unit, get: FalconSRX.() -> T) = FalconSRXProp(set, get)

    private class FalconSRXProp<T>(private val set: FalconSRX.(T) -> Unit,
                                   private val get: FalconSRX.() -> T) {
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
    var softLimitFwd by propInit<Distance>(NativeUnits(0)) { configForwardSoftLimitThreshold(it.STU, timeoutMs) }
    var softLimitRev by propInit<Distance>(NativeUnits(0)) { configReverseSoftLimitThreshold(it.STU, timeoutMs) }
    var softLimitFwdEnabled by propInit(false) { configForwardSoftLimitEnable(it, timeoutMs) }
    var softLimitRevEnabled by propInit(false) { configReverseSoftLimitEnable(it, timeoutMs) }

    var brakeMode by propInit(NeutralMode.Coast) { setNeutralMode(it) }
    var closedLoopTolerance by propInit<Distance>(NativeUnits(0)) { configAllowableClosedloopError(0, it.STU, timeoutMs) }

    var nominalFwdOutput by propInit(0.0) { configNominalOutputForward(it, timeoutMs) }
    var nominalRevOutput by propInit(0.0) { configNominalOutputReverse(it, timeoutMs) }

    var peakFwdOutput by propInit(1.0) { configPeakOutputForward(it, timeoutMs) }
    var peakRevOutput by propInit(-1.0) { configPeakOutputReverse(it, timeoutMs) }

    var openLoopRamp by propInit<Time>(Seconds(0.0)) { configOpenloopRamp(it.SEC, timeoutMs) }
    var closedLoopRamp by propInit<Time>(Seconds(0.0)) { configClosedloopRamp(it.SEC, timeoutMs) }

    var motionCruiseVelocity by propInit<Speed>(NativeUnitsPer100Ms(0)) { configMotionCruiseVelocity(it.STU, timeoutMs) }
    var motionAcceleration by propInit(0) { configMotionAcceleration(it, timeoutMs) }

    var feedbackSensor by propInit(FeedbackDevice.None) { configSelectedFeedbackSensor(it, 0, timeoutMs) }
    var peakCurrentLimit by propInit<Current>(Amps(0)) { configPeakCurrentLimit(it.amps, timeoutMs) }

    var peakCurrentLimitDuration by propInit<Time>(Milliseconds(0)) { configPeakCurrentDuration(it.MS, timeoutMs) }
    var continuousCurrentLimit by propInit<Current>(Amps(0)) { configContinuousCurrentLimit(it.amps, timeoutMs) }
    var currentLimitingEnabled by propInit(false) { enableCurrentLimit(it) }

    var voltageCompensationSaturation by propInit<Voltage>(Volts(12.0)) { configVoltageCompSaturation(it.volts, timeoutMs) }
    var voltageCompensationEnabled by propInit(false) { enableVoltageCompensation(it) }

    var sensorPosition by prop<Distance>({ setSelectedSensorPosition(it.STU, 0, timeoutMs) }) { NativeUnits(getSelectedSensorPosition(0)) }
    val sensorVelocity: Speed
        get() = NativeUnitsPer100Ms(getSelectedSensorVelocity(0))

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


