package ai.regain.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.regain.app.data.FocusState
import ai.regain.app.data.FocusTag
import ai.regain.app.data.TimerMode
import ai.regain.app.ui.components.AuroraBackground
import ai.regain.app.ui.components.FocusDial
import ai.regain.app.ui.components.RegaMascotView
import ai.regain.app.ui.components.SoundscapeSheet
import ai.regain.app.ui.components.TagSelectorDialog

@Composable
fun FocusScreen(
    focusState: FocusState,
    tags: List<FocusTag>,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onToggleStrict: () -> Unit,
    onSelectTag: (String) -> Unit,
    onAddTag: (String) -> Unit,
    onSelectMode: (TimerMode) -> Unit,
    onSelectSound: (String?) -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onOpenMenu: () -> Unit
) {
    var showTagDialog by remember { mutableStateOf(false) }
    var showSoundSheet by remember { mutableStateOf(false) }

    AuroraBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Timer Mode selector pills & Headphones Sound button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode Switcher Pills (Timer, Stopwatch, Pomodoro)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF131F18))
                        .padding(3.dp)
                ) {
                    TimerMode.values().forEach { mode ->
                        val isSelected = focusState.mode == mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFF22C55E) else Color.Transparent)
                                .clickable { onSelectMode(mode) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = mode.title,
                                color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Ambient Soundscape Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (focusState.activeSoundId != null) Color(0xFF1C3A29) else Color(0xFF131F18))
                        .border(
                            width = 1.dp,
                            color = if (focusState.activeSoundId != null) Color(0xFF22C55E) else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { showSoundSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "Soundscapes",
                        tint = if (focusState.activeSoundId != null) Color(0xFF22C55E) else Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Focus Dial matching shot_1.png
            FocusDial(
                state = focusState,
                onTagClick = { showTagDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Middle Control Row: Menu Button | Rega Mascot | Strict Lock Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hamburger Menu Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF131F18))
                        .clickable { onOpenMenu() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Center Mascot Rega (Tap to get encouragement/speech bubble!)
                RegaMascotView(
                    isStrict = focusState.isStrict
                )

                // Golden Lock / Strict Mode Toggle Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (focusState.isStrict) Color(0xFF38270A) else Color(0xFF131F18))
                        .border(
                            width = 1.dp,
                            color = if (focusState.isStrict) Color(0xFFF59E0B) else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onToggleStrict() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (focusState.isStrict) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Strict Mode",
                        tint = if (focusState.isStrict) Color(0xFFF59E0B) else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Pill Button: "Pause" / "Start Focus" / "Resume"
            val buttonText = when {
                focusState.isRunning -> "Pause"
                focusState.isPaused -> "Resume"
                else -> "Start Focus"
            }

            val buttonBg = when {
                focusState.isRunning -> Color(0xFF132219)
                else -> Color(0xFF22C55E)
            }

            val buttonTextColor = when {
                focusState.isRunning -> Color.White
                else -> Color.Black
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(buttonBg)
                    .border(
                        width = if (focusState.isRunning) 1.dp else 0.dp,
                        color = if (focusState.isRunning) Color(0xFF1E3828) else Color.Transparent,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .clickable {
                        when {
                            focusState.isRunning -> onPauseTimer()
                            focusState.isPaused -> onResumeTimer()
                            else -> onStartTimer()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buttonText,
                    color = buttonTextColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showTagDialog) {
        TagSelectorDialog(
            tags = tags,
            activeTag = focusState.activeTag,
            onTagSelected = onSelectTag,
            onAddTag = onAddTag,
            onDismiss = { showTagDialog = false }
        )
    }

    if (showSoundSheet) {
        SoundscapeSheet(
            activeSoundId = focusState.activeSoundId,
            currentVolume = focusState.soundVolume,
            onSoundSelected = onSelectSound,
            onVolumeChanged = onVolumeChanged,
            onDismiss = { showSoundSheet = false }
        )
    }
}
