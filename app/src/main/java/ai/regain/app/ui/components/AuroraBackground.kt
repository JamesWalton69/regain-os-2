package ai.regain.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import ai.regain.app.data.RegainTheme
import kotlin.random.Random

@Composable
fun AuroraBackground(
    theme: RegainTheme = RegainTheme.AURORA,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(theme.bgStart),
                        Color(theme.bgEnd),
                        Color(0xFF040A07)
                    )
                )
            )
    ) {
        // Draw subtle starry night & aurora borealis glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Starry particles with deterministic random seeds
            val starRand = Random(42)
            for (i in 0..60) {
                val x = starRand.nextFloat() * width
                val y = starRand.nextFloat() * (height * 0.7f)
                val radius = starRand.nextFloat() * 1.8f + 0.5f
                val alpha = starRand.nextFloat() * 0.6f + 0.2f
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(x, y)
                )
            }

            // Aurora green wave glow in middle background
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(theme.accent).copy(alpha = 0.22f),
                        Color(theme.accent).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.5f, height * 0.45f),
                    radius = width * 0.75f
                ),
                radius = width * 0.75f,
                center = Offset(width * 0.5f, height * 0.45f)
            )
        }

        content()
    }
}
