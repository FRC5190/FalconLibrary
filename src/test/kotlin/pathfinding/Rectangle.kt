import kotlin.math.pow

data class Rectangle(
        val x: Double,
        val y: Double,
        val w: Double,
        val h: Double
) {

    fun isIn(r: Rectangle) = x < r.x + r.w && x + w > r.x && y < r.y + r.h && y + h > r.y
}

data class Point(
        val x: Double,
        val y: Double
) {
    val distance
        get() = Math.sqrt(x.pow(2.0) + y.pow(2.0))

    fun distance(point: Point) = (this - point).distance

    operator fun plus(point: Point) = Point(this.x + point.x, this.y + point.y)
    operator fun minus(point: Point) = Point(this.x - point.x, this.y - point.y)

    operator fun div(other: Double) = Point(this.x / other, this.y / other)
}

fun List<Point>.optimize(): List<Point> {
    val newPath = mutableListOf<Point>()
    val angles = zipWithNext { one, two -> Math.atan2(two.y - one.y, two.x - one.x) }
    for ((index, point) in withIndex()) {
        if (index == 0 || index == size - 1) {
            newPath += point
            continue
        }
        val lastAngle = angles[index - 1]
        val nextAngle = angles[index]
        if(lastAngle != nextAngle) {
            newPath += point
        }
    }
    return newPath
}

fun List<Point>.optimizeMinDistance(minDistance: Double) : List<Point> {
    val newPath = mutableListOf<Point>()
    val distances = zipWithNext { one, two -> one.distance(two) }
    for ((index, point) in withIndex()) {
        if (index == 0 || index == size - 1) {
            newPath += point
            continue
        }
        val nextDistance = distances[index]
        if(nextDistance > minDistance) {
            newPath += point
        }
    }
    return newPath
}

fun List<Point>.optimizeMaxDistance(maxDistance: Double) : List<Point> {
    val newPath = mutableListOf<Point>()
    for ((one, two) in zipWithNext()) {
        val distance = one.distance(two)
        newPath += one
        if(distance > maxDistance) {
            newPath += (one + two) / 2.0
        }
    }
    newPath += last()
    return newPath
}