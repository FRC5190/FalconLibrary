package org.ghrobotics.lib.motors.rev;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;

public class CANSensorShim {
    public static void configCANEncoderonCanPIDController(CANPIDController target, CANEncoder canEncoder) {
        target.setFeedbackDevice(canEncoder);
    }
}
