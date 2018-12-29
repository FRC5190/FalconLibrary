package org.ghrobotics.lib.sensors

import com.ctre.phoenix.sensors.PigeonIMU
import com.kauailabs.navx.frc.AHRS
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.degree
import org.ghrobotics.lib.utils.Source

fun PigeonIMU.asSource(): Source<Rotation2d> = { fusedHeading.degree }
fun AHRS.asSource(): Source<Rotation2d> = { (-fusedHeading).degree }
