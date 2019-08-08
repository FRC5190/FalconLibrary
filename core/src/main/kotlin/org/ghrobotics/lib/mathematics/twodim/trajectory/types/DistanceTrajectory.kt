package org.ghrobotics.lib.mathematics.twodim.trajectory.types

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.types.VaryInterpolatable

class DistanceTrajectory<S : VaryInterpolatable<S>>(
    override val points: List<S>
) : Trajectory<SIUnit<Meter>, S> {

    private val distances: List<Double>

    init {
        val tempDistances = mutableListOf<Double>()
        tempDistances += 0.0
        points.zipWithNext { c, n -> tempDistances += tempDistances.last() + c.distance(n) }
        distances = tempDistances
    }

    override fun sample(interpolant: SIUnit<Meter>) = when {
        interpolant >= lastInterpolant -> TrajectorySamplePoint(getPoint(points.size - 1))
        interpolant.value <= 0.0 -> TrajectorySamplePoint(getPoint(0))
        else -> {
            val (index, entry) = points.asSequence()
                .withIndex()
                .first { (index, _) -> index != 0 && distances[index] >= interpolant.value }

            val prevEntry = points[index - 1]
            if (distances[index] epsilonEquals distances[index - 1]) {
                TrajectorySamplePoint(entry, index, index)
            } else {
                TrajectorySamplePoint(
                    prevEntry.interpolate(
                        entry,
                        (interpolant.value - distances[index - 1]) / (distances[index] - distances[index - 1])
                    ),
                    index - 1,
                    index
                )
            }
        }
    }

    override val firstState get() = points.first()
    override val lastState get() = points.last()

    override val firstInterpolant get() = 0.meter
    override val lastInterpolant get() = distances.last().meter

    override fun iterator() = DistanceIterator(this)
}

class DistanceIterator<S : VaryInterpolatable<S>>(
    trajectory: DistanceTrajectory<S>
) : TrajectoryIterator<SIUnit<Meter>, S>(trajectory) {
    override fun addition(a: SIUnit<Meter>, b: SIUnit<Meter>) = a + b
}