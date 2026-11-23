package ai.regain.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RegainGreenPrimary,
    onPrimary = Color.Black,
    secondary = RegainGreenVibrant,
    onSecondary = Color.Black,
    tertiary = RegainGold,
    background = RegainDark,
    surface = RegainSurface,
    onBackground = RegainTextPrimary,
    onSurface = RegainTextPrimary
)

@Composable
fun RegainTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
