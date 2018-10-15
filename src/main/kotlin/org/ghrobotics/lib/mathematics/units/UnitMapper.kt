package org.ghrobotics.lib.mathematics.units

object UnitMapper {

    val lengthMapper = SpecificUnitMapper<LengthUnits>()
    val timeMapper = SpecificUnitMapper<TimeUnits>()
    val electricCurrentMapper = SpecificUnitMapper<ElectricCurrentUnits>()
    val massMapper = SpecificUnitMapper<MassUnits>()
    val rotationMapper = SpecificUnitMapper<LengthUnits>()

    init {
        lengthMapper.apply {
            map(LengthUnits.Inch, LengthUnits.Thou, 1000.0)
            map(LengthUnits.Inch, LengthUnits.Line, 12.0)
            map(LengthUnits.Inch, LengthUnits.Meter, 0.0254)
            map(LengthUnits.Feet, LengthUnits.Inch, 12.0)
            map(LengthUnits.Yard, LengthUnits.Feet, 3.0)
            map(LengthUnits.Mile, LengthUnits.Feet, 5280.0)
            map(LengthUnits.League, LengthUnits.Mile, 3.0)
            map(LengthUnits.NauticalMile, LengthUnits.Meter, 1852.0)
            map(LengthUnits.LightYear, LengthUnits.Meter, 9460730472580800.0)
        }
        timeMapper.apply {
            map(TimeUnits.Minute, TimeUnits.Second, 60.0)
            map(TimeUnits.Hour, TimeUnits.Minute, 60.0)
            map(TimeUnits.Day, TimeUnits.Hour, 24.0)
            map(TimeUnits.Week, TimeUnits.Day, 7.0)
            map(TimeUnits.Moment, TimeUnits.Second, 90.0)
        }
        rotationMapper.apply {

        }
    }

    class SpecificUnitMapper<T> {

        private val unitMap = mutableMapOf<Pair<T, T>, Double>()

        fun map(from: T, to: T, factor: Double) {
            unitMap[from to to] = factor
        }

        fun conversionFactor(from: T, to: T) =
            unitMap[from to to]
                ?: unitMap[to to from]?.let { 1.0 / it }
                ?: smartConversionFactor(from, to)
                ?: throw IllegalArgumentException("Unable to find conversion factor between $from and $to")

        private fun smartConversionFactor(from: T, to: T): Double? {
            val path = smartConversionFactor(emptyList(), from, to) ?: return null
            var factor = 1.0
            var current = from
            for (key in path.reversed()) {
                current = if (key.first == current) {
                    factor *= unitMap[key]!!
                    key.second
                } else {
                    factor /= unitMap[key]!!
                    key.first
                }
            }
            unitMap[from to to] = factor
            return factor
        }

        private fun smartConversionFactor(
            cameFrom: List<Pair<T, T>>,
            current: T,
            to: T
        ): List<Pair<T, T>>? {
            val possiblePaths = unitMap.keys.filter {
                !cameFrom.contains(it) && (it.first == current || it.second == current)
            }
            var bestPath: List<Pair<T, T>>? = null
            for (key in possiblePaths) {
                if (key.first == to || key.second == to) return listOf(key)
                val newCurrent = if (key.first == current) key.second else key.first
                val path = smartConversionFactor(cameFrom + key, newCurrent, to)
                    ?: continue
                if (bestPath == null || path.size + 1 < bestPath.size)
                    bestPath = path + key
            }
            return bestPath
        }

        fun convert(
            value: Double,
            from: T,
            to: T
        ): Double {
            if (from == to) return value
            return conversionFactor(from, to) * value
        }

    }

}