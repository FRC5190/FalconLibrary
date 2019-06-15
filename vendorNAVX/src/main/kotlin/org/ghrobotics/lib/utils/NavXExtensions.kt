import com.kauailabs.navx.frc.AHRS
import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d
import org.ghrobotics.lib.utils.Source

fun AHRS.asSource(): Source<Rotation2d> = { Rotation2d.fromDegrees(-fusedHeading.toDouble()) }