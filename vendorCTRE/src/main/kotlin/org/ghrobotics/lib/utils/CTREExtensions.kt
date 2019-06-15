import com.ctre.phoenix.CANifier
import com.ctre.phoenix.sensors.PigeonIMU
import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d
import org.ghrobotics.lib.utils.Source
import java.awt.Color

fun CANifier.setLEDOutput(color: Color) = setLEDOutput(color.red, color.green, color.blue)

fun CANifier.setLEDOutput(r: Int, g: Int, b: Int) {
    setLEDOutput(r * (1.0 / 255.0), CANifier.LEDChannel.LEDChannelB)
    setLEDOutput(g * (1.0 / 255.0), CANifier.LEDChannel.LEDChannelA)
    setLEDOutput(b * (1.0 / 255.0), CANifier.LEDChannel.LEDChannelC)
}

fun PigeonIMU.asSource(): Source<Rotation2d> = { Rotation2d.fromDegrees(fusedHeading) }