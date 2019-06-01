import com.kauailabs.navx.frc.AHRS
import org.ghrobotics.lib.mathematics.units.UnboundedRotation
import org.ghrobotics.lib.mathematics.units.degree
import org.ghrobotics.lib.utils.Source

fun AHRS.asSource(): Source<UnboundedRotation> = { -fusedHeading.degree }