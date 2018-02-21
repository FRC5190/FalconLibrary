package frc.team5190.lib

import com.ctre.phoenix.motorcontrol.*
import com.ctre.phoenix.motorcontrol.can.TalonSRX

@Suppress("unused", "MemberVisibilityCanBePrivate")
class FalconTalonSRX(id: Int, private val timeoutMs: Int = 10) : TalonSRX(id) {

    internal var p = 0.0
        set(value) {
            config_kP(0, value, timeoutMs)
            field = value
        }

    internal var i = 0.0
        set(value) {
            config_kI(0, value, timeoutMs)
            field = value
        }

    internal var d = 0.0
        set(value) {
            config_kD(0, value, timeoutMs)
            field = value
        }

    internal var f = 0.0
        set(value) {
            config_kF(0, value, timeoutMs)
            field = value
        }

    internal var sensorPhase = false
        set(value) {
            setSensorPhase(value)
            field = value
        }

    internal var overrideLimitSwitchesEnable = false
        set(value) {
            overrideLimitSwitchesEnable(value)
            field = value
        }

    internal var softLimitFwd = 0
        set(value) {
            configForwardSoftLimitThreshold(value, timeoutMs)
            field = value
        }

    internal var softLimitRev = 0
        set(value) {
            configReverseSoftLimitThreshold(value, timeoutMs)
            field = value
        }

    internal var softLimitFwdEnabled = false
        set(value) {
            configForwardSoftLimitEnable(value, timeoutMs)
            field = value
        }

    internal var softLimitRevEnabled = false
        set(value) {
            configReverseSoftLimitEnable(value, timeoutMs)
            field = value
        }

    internal var neutralMode = NeutralMode.Coast
        set(value) {
            setNeutralMode(value)
            field = value
        }

    internal var closedLoopTolerance = 0
        set(value) {
            configAllowableClosedloopError(0, value, timeoutMs)
            field = value
        }

    internal var nominalFwdOutput = 0.0
        set(value) {
            configNominalOutputForward(value, timeoutMs)
            field = value
        }

    internal var nominalRevOutput = 0.0
        set(value) {
            configNominalOutputReverse(value, timeoutMs)
            field = value
        }

    internal var nominalOutput = 0.0
        set(value) {
            nominalFwdOutput = value
            nominalRevOutput = -value
            field = value
        }

    internal var peakFwdOutput = 1.0
        set(value) {
            configPeakOutputForward(value, timeoutMs)
            field = value
        }

    internal var peakRevOutput = -1.0
        set(value) {
            configPeakOutputReverse(value, timeoutMs)
            field = value
        }

    internal var peakOutput = 1.0
        set(value) {
            peakFwdOutput = value
            peakRevOutput = -value
            field = value
        }

    internal var motionCruiseVelocity = 0
        set(value) {
            configMotionCruiseVelocity(value, timeoutMs)
            field = value
        }

    internal var motionAcceleration = 0
        set(value) {
            configMotionAcceleration(value, timeoutMs)
            field = value
        }

    internal var feedbackSensor = FeedbackDevice.None
        set(value) {
            configSelectedFeedbackSensor(value, 0, timeoutMs)
            field = value
        }

    internal var lowPeakCurrent = 0
    internal var highPeakCurrent = 0
    internal var peakCurrentDuration = 0
    internal var limitingReductionFactor = 0
    internal var enableCurrentLimiting = false


    private val currentBuffer = ArrayList<Double>(25)

    private val average: Double
        get() {
            return if (numElements == 0)
                0.0
            else
                sum / numElements
        }

    private val motorState: MotorState
        get () {
            iterator = if (average > highPeakCurrent) iterator + 1 else 0
            return when {
                average < lowPeakCurrent && iterator < peakCurrentDuration / 20 -> MotorState.GOOD
                average < highPeakCurrent && iterator < peakCurrentDuration / 20 -> MotorState.OK
                average > highPeakCurrent && iterator < peakCurrentDuration / 20 -> MotorState.OK
                else -> MotorState.STALL
            }
        }


    private var numElements = 0
    private var sum = 0.0
    private var iterator = 0
    private var stalled = false


    internal fun periodic(limitCurrent: Boolean = false): MotorState {
        return if (limitCurrent) {
            limitCurrent()
        }
        else MotorState.UNKNOWN
    }

    internal fun limitCurrent(): MotorState {
        addToCurrentBuffer(outputCurrent)
        return motorState
    }

    internal fun setLimitSwitch(source: LimitSwitchSource, normal: LimitSwitchNormal) {
        configForwardLimitSwitchSource(source, normal, timeoutMs)
        configReverseLimitSwitchSource(source, normal, timeoutMs)
    }

    internal fun setStatusFramePeriod(frame: StatusFrameEnhanced, periodMs: Int) {
        setStatusFramePeriod(frame, periodMs, timeoutMs)
    }


    private fun addToCurrentBuffer(amps: Double) {
        if (numElements > 24) {
            sum -= currentBuffer[24]
            currentBuffer.removeAt(24)
            numElements--
        }
        sum += amps
        currentBuffer.add(0, amps)
        numElements++
    }
}

enum class MotorState {
    GOOD, OK, STALL, UNKNOWN
}

