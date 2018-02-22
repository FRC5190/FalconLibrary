package frc.team5190.lib

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal
import com.ctre.phoenix.motorcontrol.LimitSwitchSource
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import frc.team5190.lib.utils.EvictingQueue

class FalconTalonSRX(id: Int, private val timeoutMs: Int = 10) : TalonSRX(id) {

    var p = 0.0
        set(value) {
            config_kP(0, value, timeoutMs)
            field = value
        }

    var i = 0.0
        set(value) {
            config_kI(0, value, timeoutMs)
            field = value
        }

    var d = 0.0
        set(value) {
            config_kD(0, value, timeoutMs)
            field = value
        }

    var f = 0.0
        set(value) {
            config_kF(0, value, timeoutMs)
            field = value
        }

//    var sensorPhase = false
//        set(value) {
//            setSensorPhase(value)
//            field = value
//        }

    var overrideLimitSwitchesEnable = false
        set(value) {
            overrideLimitSwitchesEnable(value)
            field = value
        }

    var softLimitFwd = 0
        set(value) {
            configForwardSoftLimitThreshold(value, timeoutMs)
            field = value
        }

    var softLimitRev = 0
        set(value) {
            configReverseSoftLimitThreshold(value, timeoutMs)
            field = value
        }

    var softLimitFwdEnabled = false
        set(value) {
            configForwardSoftLimitEnable(value, timeoutMs)
            field = value
        }

    var softLimitRevEnabled = false
        set(value) {
            configReverseSoftLimitEnable(value, timeoutMs)
            field = value
        }

//    var neutralMode = NeutralMode.Coast
//        set(value) {
//            setNeutralMode(value)
//            field = value
//        }

    var closedLoopTolerance = 0
        set(value) {
            configAllowableClosedloopError(0, value, timeoutMs)
            field = value
        }

    var nominalFwdOutput = 0.0
        set(value) {
            configNominalOutputForward(value, timeoutMs)
            field = value
        }

    var nominalRevOutput = 0.0
        set(value) {
            configNominalOutputReverse(value, timeoutMs)
            field = value
        }

    var nominalOutput = 0.0
        set(value) {
            nominalFwdOutput = value
            nominalRevOutput = -value
            field = value
        }

    var peakFwdOutput = 1.0
        set(value) {
            configPeakOutputForward(value, timeoutMs)
            field = value
        }

    var peakRevOutput = -1.0
        set(value) {
            configPeakOutputReverse(value, timeoutMs)
            field = value
        }

    var peakOutput = 1.0
        set(value) {
            peakFwdOutput = value
            peakRevOutput = -value
            field = value
        }

    var motionCruiseVelocity = 0
        set(value) {
            configMotionCruiseVelocity(value, timeoutMs)
            field = value
        }

    var motionAcceleration = 0
        set(value) {
            configMotionAcceleration(value, timeoutMs)
            field = value
        }

    var feedbackSensor = FeedbackDevice.None
        set(value) {
            configSelectedFeedbackSensor(value, 0, timeoutMs)
            field = value
        }

    var lowPeakCurrent = 0
    var highPeakCurrent = 0
    var peakCurrentDuration = 0
    var limitingReductionFactor = 0
    var enableCurrentLimiting = false

    private val currentBuffer = EvictingQueue<Double>(25)

    private val currentAverage: Double
        get() = currentBuffer.average()

    private val motorState: MotorState
        get () {
            iterator = if (currentAverage > highPeakCurrent) iterator + 1 else 0
            return when {
                currentAverage < lowPeakCurrent && iterator < peakCurrentDuration / 20 -> MotorState.GOOD
                currentAverage < highPeakCurrent && iterator < peakCurrentDuration / 20 -> MotorState.OK
                currentAverage > highPeakCurrent && iterator < peakCurrentDuration / 20 -> MotorState.OK
                else -> MotorState.STALL
            }
        }

    private var iterator = 0
    private var stalled = false

    fun periodic(limitCurrent: Boolean = false): MotorState {
        return if (limitCurrent) {
            limitCurrent()
        } else MotorState.UNKNOWN
    }

    private fun limitCurrent(): MotorState {
        addToCurrentBuffer(outputCurrent)
        return motorState
    }

    fun setLimitSwitch(source: LimitSwitchSource, normal: LimitSwitchNormal) {
        configForwardLimitSwitchSource(source, normal, timeoutMs)
        configReverseLimitSwitchSource(source, normal, timeoutMs)
    }

    fun setStatusFramePeriod(frame: StatusFrameEnhanced, periodMs: Int) {
        setStatusFramePeriod(frame, periodMs, timeoutMs)
    }


    private fun addToCurrentBuffer(amps: Double) = currentBuffer.add(amps)
}

enum class MotorState {
    GOOD, OK, STALL, UNKNOWN
}

