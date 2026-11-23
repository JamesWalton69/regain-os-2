package ai.regain.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import ai.regain.app.data.Participant
import kotlinx.coroutines.delay

@Composable
fun MultiplayerScreen(
    onBack: () -> Unit
) {
    var roomName by remember { mutableStateOf("IIT Aspirants") }
    var activeCount by remember { mutableIntStateOf(520203) }
    var floatingReaction by remember { mutableStateOf<String?>(null) }

    val participants = remember {
        listOf(
            Participant("p1", "You", 165, "#22C55E", isYou = true),
            Participant("p2", "Rohan", 150, "#F59E0B"),
            Participant("p3", "Ayaan Khan", 135, "#A855F7"),
            Participant("p4", "Rajesh Kumar", 80, "#3B82F6"),
            Participant("p5", "Nisha Sharma", 75, "#EC4899"),
            Participant("p6", "Aarav Mehta", 65, "#EAB308"),
            Participant("p7", "Priya Singh", 60, "#06B6D4"),
            Participant("p8", "Tanmay Sen", 55, "#84CC16"),
            Participant("p9", "Ananya Verma", 50, "#F43F5E")
        )
    }

    val reactions = listOf("❤️", "👏", "💯", "🔥", "😮")

    LaunchedEffect(floatingReaction) {
        if (floatingReaction != null) {
            delay(1200L)
            floatingReaction = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090E11))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 44.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Live Focus with friends",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Room Title matching shot_4.png
            Text(
                text = roomName,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Live Pill: "🟢 520,203 focusing"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF132A1C))
                    .border(1.dp, Color(0xFF1F482F), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🟢 ${String.format("%,d", activeCount)} focusing",
                    color = Color(0xFF86EFAC),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Participants 3-Column Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(22.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(participants) { participant ->
                    ParticipantCard(participant = participant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Emoji Reaction Bar at Bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF121B15))
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                reactions.forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B2C22))
                            .clickable {
                                floatingReaction = emoji
                                activeCount += 1
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                    }
                }
            }
        }

        // Floating Reaction Burst
        AnimatedVisibility(
            visible = floatingReaction != null,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(400)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            floatingReaction?.let { emoji ->
                Text(
                    text = emoji,
                    fontSize = 72.sp
                )
            }
        }
    }
}

@Composable
private fun ParticipantCard(participant: Participant) {
    val hours = participant.focusMinutes / 60
    val mins = participant.focusMinutes % 60
    val durationText = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

    val avatarColor = try {
        Color(android.graphics.Color.parseColor(participant.avatarColorHex))
    } catch (e: Exception) {
        Color(0xFF22C55E)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(avatarColor.copy(alpha = 0.25f))
                .border(
                    width = if (participant.isYou) 2.5.dp else 1.dp,
                    color = if (participant.isYou) Color(0xFF22C55E) else avatarColor.copy(alpha = 0.6f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Avatar letter / emoji
            Text(
                text = participant.name.take(1),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = participant.name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (participant.isYou) FontWeight.Bold else FontWeight.Medium
        )

        Text(
            text = durationText,
            color = if (participant.isYou) Color(0xFF86EFAC) else Color.White.copy(alpha = 0.65f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
