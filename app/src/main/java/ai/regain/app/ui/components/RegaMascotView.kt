package ai.regain.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.regain.app.R
import kotlinx.coroutines.delay

@Composable
fun RegaMascotView(
    modifier: Modifier = Modifier,
    isStrict: Boolean = false,
    onClick: () -> Unit = {}
) {
    var showTip by remember { mutableStateOf(false) }
    var currentTipIndex by remember { mutableStateOf(0) }
    var isTapped by remember { mutableStateOf(false) }

    val tips = listOf(
        "Rega believes in you! Stay locked in 🌿",
        "Don't open that Reel! 3+ hours saved this week 🚀",
        "Deep focus makes exams feel easy ✨",
        "One Pomodoro round at a time!",
        "Strict mode on: No cheat, no escape 🔒"
    )

    val scale by animateFloatAsState(
        targetValue = if (isTapped) 1.25f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "rega_bounce"
    )

    LaunchedEffect(showTip) {
        if (showTip) {
            delay(3200L)
            showTip = false
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Speech Bubble Tip
        AnimatedVisibility(
            visible = showTip,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-58).dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF1E2D25), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tips[currentTipIndex],
                    color = Color(0xFF86EFAC),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Mascot Circular Avatar Button
        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Color(0xFF162A1F))
                .clickable {
                    isTapped = true
                    currentTipIndex = (currentTipIndex + 1) % tips.size
                    showTip = true
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_rega_mascot),
                contentDescription = "Rega Focus Mascot",
                modifier = Modifier.size(42.dp)
            )
        }
    }

    LaunchedEffect(isTapped) {
        if (isTapped) {
            delay(150)
            isTapped = false
        }
    }
}
