package am.nsardaryan.flockmeplease

import am.nsardaryan.flockmeplease.ui.theme.FlockmepleaseTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlockmepleaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlockScreen()
                }
            }
        }
    }
}

@Composable
fun FlockScreen() {

    var boidsCount by remember { mutableIntStateOf(60) }
    val flock = remember { Flock() }

    LaunchedEffect(boidsCount) {
        flock.generateFlock(boidsCount)
        while (true) {
            withFrameNanos {
                flock.update(it)
            }
        }
    }

    Column() {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(bottom = 20.dp)
                .onGloballyPositioned { coordinates ->
                    flock.canvasHeight = coordinates.size.height - 100
                    flock.canvasWidth = coordinates.size.width - 100
                }
        ) {
            flock.boids.forEach { b ->
                val mainBoidColor = b.getColorForBoid(flock.currentBoid)
                drawCircle(
                    color = mainBoidColor,
                    radius = 5f,
                    center = b.position
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(mainBoidColor, Color.Transparent),
                        center = b.position,
                        radius = VISIBLE_RANGE
                    ),
                    alpha = 0.01f,
                    radius = VISIBLE_RANGE,
                    center = b.position
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(mainBoidColor, Color.Transparent),
                        center = b.position,
                        radius = PROTECTED_RANGE
                    ),
                    alpha = 0.01f,
                    radius = PROTECTED_RANGE,
                    center = b.position
                )
//                 drawArrow(b.position, b.speed.times(10f), color = mainBoidColor)
            }
        }

        Column(modifier = Modifier.padding(25.dp)) {
            Text(text = "Forces: ${flock.currentBoid?.forces}")
            Text(text = "Speed: ${flock.currentBoid?.speed}")

            Text(text = "Boids count: $boidsCount")
            Slider(value = boidsCount.toFloat(),
                valueRange = 0f..250f,
                onValueChange = { boidsCount = it.toInt() })

            Text(text = "Separation: ${flock.separationValue}")
            Slider(value = flock.separationValue,
                valueRange = 0f..1f,
                onValueChange = { flock.separationValue = it })

            Text(text = "Cohesion: ${flock.cohesionValue}")
            Slider(value = flock.cohesionValue,
                valueRange = 0f..1f,
                onValueChange = { flock.cohesionValue = it })

            Text(text = "Align: ${flock.alignValue}")
            Slider(value = flock.alignValue,
                valueRange = 0f..1f,
                onValueChange = { flock.alignValue = it })
        }

    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FLockPreview() {
    FlockmepleaseTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            FlockScreen()
        }
    }
}