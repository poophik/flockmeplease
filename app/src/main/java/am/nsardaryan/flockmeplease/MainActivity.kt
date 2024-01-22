@file:OptIn(ExperimentalMaterial3Api::class)

package am.nsardaryan.flockmeplease

import am.nsardaryan.flockmeplease.ui.theme.Background
import am.nsardaryan.flockmeplease.ui.theme.BoidColor
import am.nsardaryan.flockmeplease.ui.theme.FlockmepleaseTheme
import am.nsardaryan.flockmeplease.ui.theme.Typography
import am.nsardaryan.flockmeplease.ui.theme.BottomColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


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

    BottomSheetScaffold(
        sheetContent = {
            FlockSettings(
                count = boidsCount,
                onCountChange = { boidsCount = it },
                separation = flock.separationValue,
                onSeparationChange = { flock.separationValue = it },
                cohesion = flock.cohesionValue,
                onCohesionChange = { flock.cohesionValue = it },
                align = flock.alignValue,
                onAlignChange = { flock.alignValue = it }
            )
        },
        sheetDragHandle = null,
        sheetContainerColor = BottomColor,
        sheetShape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
        sheetPeekHeight = 72.dp
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Background)
                .onGloballyPositioned { coordinates ->
                    flock.canvasHeight = coordinates.size.height
                    flock.canvasWidth = coordinates.size.width
                }
        ) {
            flock.boids.forEach { b ->
                drawCircle(
                    color = BoidColor,
                    radius = 2f,
                    center = b.position
                )
            }
        }
    }
}

@Composable
fun FlockSettings(
    count: Int,
    onCountChange: (Int) -> Unit,
    separation: Float,
    onSeparationChange: (Float) -> Unit,
    cohesion: (Float),
    onCohesionChange: (Float) -> Unit,
    align: Float,
    onAlignChange: (Float) -> Unit,
) {
    // Sheet content
    Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 25.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Settings", style = Typography.headlineMedium)
            IconButton(onClick = {
                onSeparationChange.invoke(0f)
                onCohesionChange.invoke(0f)
                onAlignChange.invoke(0f)
            }) {
                Icon(imageVector = Icons.Filled.Refresh, "Reset")

            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Boids count: $count")
        Slider(
            value = count.toFloat(),
            colors = SliderDefaults.colors(),
            valueRange = 0f..250f,
            onValueChange = { onCountChange.invoke(it.toInt()) })

        Text(text = "Separation: ${(separation * 100).roundToInt()}")
        Slider(value = separation,
            valueRange = 0f..1f,
            onValueChange = { onSeparationChange.invoke(it) })

        Text(text = "Cohesion: ${(cohesion * 100).roundToInt()}")
        Slider(value = cohesion,
            valueRange = 0f..1f,
            onValueChange = { onCohesionChange.invoke(it) })

        Text(text = "Align: ${(align * 100).roundToInt()}")
        Slider(value = align,
            valueRange = 0f..1f,
            onValueChange = { onAlignChange.invoke(it) })
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