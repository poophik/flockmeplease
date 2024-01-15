package am.nsardaryan.flockmeplease

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt


class Flock() {
    var boids = mutableStateListOf<Boid>()
    var separationValue by mutableFloatStateOf(1f)
    var cohesionValue by mutableFloatStateOf(1f)
    var alignValue by mutableFloatStateOf(1f)
    var canvasWidth by mutableIntStateOf(0)
    var canvasHeight by mutableIntStateOf(0)

    var currentBoid by mutableStateOf<Boid?>(null)

    private var prevTime = 0L

    fun generateTestFlock() {
        boids.clear()
        val testBoids = testBoids(this, canvasWidth, canvasHeight)
        boids.addAll(testBoids)
    }

    fun generateFlock(population: Int) {
        boids.clear()
        repeat(population) {
            val randomPosition = Offset(
                x = Random.nextFloat() * canvasWidth, y = Random.nextFloat() * canvasHeight
            )
            boids.add(Boid(flock = this, position = randomPosition))
        }
        currentBoid = boids[nextInt(boids.size)]
    }

    fun update(time: Long) {
        if (prevTime == 0L) {
            prevTime = time
            return
        }
        val delta = time - prevTime
        val floatDelta = (delta / 1E8).toFloat()
        prevTime = time

        val visibleNeighborsMap: HashMap<Boid, MutableList<Boid>> = hashMapOf()
        val protectedNeighborsMap: HashMap<Boid, MutableList<Boid>> = hashMapOf()

        for (boid in boids) {

            if (!visibleNeighborsMap.containsKey(boid) && !protectedNeighborsMap.containsKey(boid)) {
                visibleNeighborsMap[boid] = mutableListOf()
                protectedNeighborsMap[boid] = mutableListOf()
            }

            for (other in boids) {
                if (boid != other) {

                    if (protectedNeighborsMap[other]?.contains(boid) == true) {
                        protectedNeighborsMap[boid]?.add(other)
                    } else if (visibleNeighborsMap[other]?.contains(boid) == true) {
                        visibleNeighborsMap[boid]?.add(other)
                    } else {
                        val squaredDistance = boid.position.minus(other.position).getDistanceSquared()
                        if (squaredDistance <= PROTECTED_RANGE * PROTECTED_RANGE) {
                            protectedNeighborsMap[boid]?.add(other)
                        } else if (squaredDistance <= VISIBLE_RANGE * VISIBLE_RANGE) {
                            visibleNeighborsMap[boid]?.add(other)
                        }
                    }
                }
            }

            boid.update(
                protectedNeighbors = protectedNeighborsMap[boid]?.toList(),
                visibleNeighbors = visibleNeighborsMap[boid]?.toList(),
                realDelta = floatDelta
            )
            protectedNeighborsMap.clear()
            visibleNeighborsMap.clear()
        }
    }
}