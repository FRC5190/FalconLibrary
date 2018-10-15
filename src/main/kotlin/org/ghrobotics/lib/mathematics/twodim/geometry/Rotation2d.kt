package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.Rotation2dImpl


// A 2d rotation in space can be represented by a single unit type (degrees and radians) unlike the other
// geometric types. Therefore, the implementation of rotation transformations are embedded within the units
// itself. This class remains so that all geometric transformations are in the same package. (for my sanity)

@Suppress("FunctionName")
fun Rotation2d(x: Double, y: Double, normalize: Boolean): Rotation2d =
        Rotation2dImpl(x, y, normalize)