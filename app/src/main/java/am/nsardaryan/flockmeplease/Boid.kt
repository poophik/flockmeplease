package am.nsardaryan.flockmeplease

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlin.random.Random.Default.nextInt


class Boid(
    val flock: Flock,
    position: Offset,
) {
    var position by mutableStateOf(position)
    var speed by mutableStateOf(Offset.Zero)
    var forces by mutableStateOf(Offset.Zero)

    init {
        speed = Offset(
            x = nextInt(-MAX_SPEED.toInt(), MAX_SPEED.toInt()).toFloat(),
            y = nextInt(-MAX_SPEED.toInt(), MAX_SPEED.toInt()).toFloat()
        )
    }

    private fun separation(neighbors: List<Boid>): Offset {
        var steering = Offset.Zero
        neighbors.forEach { n ->
            steering = steering.plus(position.minus(n.position))
        }
        return if (neighbors.isNotEmpty()) (steering.div(neighbors.size.toFloat())).limit(MAX_FORCE) else steering
    }

    private fun cohesion(neighbors: List<Boid>): Offset {
        var steering = Offset.Zero
        neighbors.forEach { n ->
            steering = steering.plus(n.position)
        }
        return if (neighbors.isNotEmpty()) (steering.div(neighbors.size.toFloat())).minus(position).limit(MAX_FORCE) else steering
    }

    private fun align(neighbors: List<Boid>): Offset {
        var steering = Offset.Zero
        neighbors.forEach { n ->
            steering = steering.plus(n.speed)
        }
        return if (neighbors.isNotEmpty()) speed.plus(steering.div(neighbors.size.toFloat())).limit(MAX_FORCE) else steering
    }

    fun update(protectedNeighbors: List<Boid>, visibleNeighbors: List<Boid>, realDelta: Float) {
        forces = separation(protectedNeighbors).times(flock.separationValue)
                 .plus(cohesion(visibleNeighbors).times(flock.cohesionValue))
                 .plus(align(visibleNeighbors).times(flock.alignValue))

        speed = speed.plus(forces.times(realDelta)).limit(MAX_SPEED)
        position = position.plus(speed).checkEdge(flock.canvasWidth, flock.canvasHeight)
    }
}
