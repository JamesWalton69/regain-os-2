package ai.regain.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.regain.app.data.LeaderboardItem

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit
) {
    val items = remember {
        listOf(
            LeaderboardItem(1, "Zaid", 1400, "#22C55E"), // 23h 20m
            LeaderboardItem(2, "Sara", 1155, "#A855F7"), // 19h 15m
            LeaderboardItem(3, "Tariq", 1155, "#3B82F6"), // 19h 15m
            LeaderboardItem(4, "Amina", 960, "#EC4899"),   // 16h
            LeaderboardItem(5, "Rami", 815, "#F59E0B"),    // 13h 35m
            LeaderboardItem(6, "Omar", 600, "#EAB308"),    // 10h
            LeaderboardItem(7, "You", 525, "#10B981", isUser = true, streakDays = 7),
            LeaderboardItem(8, "Kavya", 490, "#06B6D4"),
            LeaderboardItem(9, "Dev", 430, "#84CC16")
        )
    }

    val first = items.firstOrNull { it.rank == 1 }
    val second = items.firstOrNull { it.rank == 2 }
    val third = items.firstOrNull { it.rank == 3 }
    val others = items.filter { it.rank > 3 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090E11))
            .padding(horizontal = 20.dp)
            .padding(top = 44.dp, bottom = 20.dp)
    ) {
        // Top Header
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
                text = "Compete in Leaderboard",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Top 3 Podium matching shot_5.png
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rank 1 (Top Center)
            first?.let {
                PodiumAvatar(
                    item = it,
                    badgeEmoji = "🥇",
                    badgeColor = Color(0xFFF59E0B),
                    size = 90.dp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Ranks 2 and 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                second?.let {
                    PodiumAvatar(
                        item = it,
                        badgeEmoji = "🥈",
                        badgeColor = Color(0xFFCBD5E1),
                        size = 78.dp
                    )
                }
                third?.let {
                    PodiumAvatar(
                        item = it,
                        badgeEmoji = "🥉",
                        badgeColor = Color(0xFFD97706),
                        size = 78.dp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Ranked List (4, 5, 6...)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(others) { item ->
                val hours = item.focusMinutes / 60
                val mins = item.focusMinutes % 60
                val timeStr = if (mins > 0) "${hours}h ${mins}m" else "${hours}h"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (item.isUser) Color(0xFF142A1D) else Color(0xFF101713))
                        .border(
                            width = if (item.isUser) 1.5.dp else 0.dp,
                            color = if (item.isUser) Color(0xFF22C55E) else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.rank.toString(),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(28.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(item.avatarColorHex)).copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.name.take(1),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (item.isUser) "${item.name} (You)" else item.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = if (item.isUser) FontWeight.Bold else FontWeight.Medium
                        )
                        if (item.isUser) {
                            Text(
                                text = "🔥 ${item.streakDays} day streak",
                                color = Color(0xFFF59E0B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = timeStr,
                        color = Color(0xFF86EFAC),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumAvatar(
    item: LeaderboardItem,
    badgeEmoji: String,
    badgeColor: Color,
    size: androidx.compose.ui.unit.Dp
) {
    val hours = item.focusMinutes / 60
    val mins = item.focusMinutes % 60
    val timeStr = if (mins > 0) "${hours}h ${mins}m" else "${hours}h"

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(item.avatarColorHex)).copy(alpha = 0.25f))
                    .border(2.dp, badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.name.take(1),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = badgeEmoji, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF16231B))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = timeStr,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
