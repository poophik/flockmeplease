package am.nsardaryan.flockmeplease

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

fun testBoids(flock: Flock, canvasWidth: Int, canvasHeight: Int) = listOf(
    // center
    Boid(flock, Offset((canvasWidth / 2).toFloat(), (canvasHeight / 2).toFloat())),
    // top
    Boid(flock, Offset((canvasWidth / 2).toFloat(), (canvasHeight / 2) - 100f)),
    // bottom
//    Boid(flock, Offset((canvasWidth / 2).toFloat(), (canvasHeight / 2) + 100f)),
    // left
//    Boid(flock, Offset((canvasWidth / 2) - 100f, (canvasHeight / 2).toFloat())),
    // right
//    Boid(flock, Offset((canvasWidth / 2) + 100f, (canvasHeight / 2).toFloat())),
)

fun Boid.findNeighbors(boids: List<Boid>, range: Float): List<Boid> {
    return boids.filter { b ->
        b != this && this.position.minus(b.position).getDistanceSquared() <= (range * range)
    }
}

fun Offset.checkEdge(width: Int, height: Int): Offset {
    var (x, y) = this
    if (x > width) {
        x = 0f
    } else if (x < 0f) {
        x = width.toFloat()
    }

    if (y > height) {
        y = 0f
    } else if (y < 0f) {
        y = height.toFloat()
    }
    return Offset(x, y)
}

fun Offset.setMag(magnitude: Float): Offset {
    return Offset(
        x = this.x * magnitude / this.getDistance(),
        y = this.y * magnitude / this.getDistance()
    )
}

fun Offset.limit(limit: Float): Offset {
    return if (this.getDistanceSquared() > (limit * limit)) this.setMag(limit) else this
}

fun Boid.getColorForBoid(testBoid: Boid?): Color {
    return if (this == testBoid) Color.Blue else Color.Red
}

fun DrawScope.drawArrow(
    center: Offset,
    vector: Offset,
    color: Color = Color.Red,
    arrowSize: Float = 5f
) {
    this.drawLine(color, center, center.plus(vector))
    this.drawCircle(color = color, radius = arrowSize, center = center.plus(vector))
}


const val VISIBLE_RANGE = 200f
const val PROTECTED_RANGE = 25f
const val MAX_SPEED = 10f
const val MAX_FORCE = 2f