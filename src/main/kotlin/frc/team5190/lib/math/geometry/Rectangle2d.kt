package frc.team5190.lib.math.geometry

data class Rectangle2d(
        val minCorner: Translation2d,
        val maxCorner: Translation2d
) {

    constructor(x: Double, y: Double, w: Double, h: Double) : this(Translation2d(x, y), Translation2d(x + w, y + h))


    val x get() = minCorner.x
    val y get() = minCorner.y
    val w get() = maxCorner.x - minCorner.x
    val h get() = maxCorner.y - minCorner.y

    fun isIn(r: Rectangle2d) = x < r.x + r.w && x + w > r.x && y < r.y + r.h && y + h > r.y
}