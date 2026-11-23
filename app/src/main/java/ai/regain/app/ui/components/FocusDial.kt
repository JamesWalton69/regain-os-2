package ai.regain.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.regain.app.data.FocusState
import ai.regain.app.data.TimerMode

@Composable
fun FocusDial(
    state: FocusState,
    onTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = when (state.mode) {
        TimerMode.STOPWATCH -> 1f
        else -> if (state.targetSeconds > 0) {
            (state.targetSeconds - state.elapsedSeconds).toFloat() / state.targetSeconds.toFloat()
        } else 1f
    }.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "dial_progress"
    )

    val remainingSecs = if (state.mode == TimerMode.STOPWATCH) {
        state.elapsedSeconds
    } else {
        maxOf(0, state.targetSeconds - state.elapsedSeconds)
    }

    val mins = remainingSecs / 60
    val secs = remainingSecs % 60
    val timeFormatted = String.format("%02d:%02d", mins, secs)

    Box(
        modifier = modifier
            .size(310.dp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Dial Canvas Arc & Glowing Border
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.5.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2

            // Base subtle ring
            drawCircle(
                color = Color.White.copy(alpha = 0.18f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Active progress arc
            if (state.mode != TimerMode.STOPWATCH) {
                drawArc(
                    color = Color.White.copy(alpha = 0.95f),
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Deep Inner Circle Card
        Box(
            modifier = Modifier
                .size(290.dp)
                .clip(CircleShape)
                .clickable { onTagClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Tag Selector Dropdown ("Study v")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onTagClick() }
                ) {
                    Text(
                        text = state.activeTag,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select Tag",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Mode Subtitle ("Timer", "Pomodoro Round 1/4", "Break")
                Text(
                    text = when {
                        state.isBreak -> "☕ Break Time"
                        state.mode == TimerMode.POMODORO -> "Pomodoro • Round ${state.pomodoroRound}/${state.totalRounds}"
                        state.mode == TimerMode.STOPWATCH -> "Stopwatch"
                        else -> "Timer"
                    },
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Giant Bold Digital Timer matching shot_1.png
                Text(
                    text = timeFormatted,
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1).sp
                )
            }
        }
    }
}
