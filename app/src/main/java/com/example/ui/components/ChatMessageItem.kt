package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.theme.StatusGreen

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onViewCodeClick: (String) -> Unit = {},
    onSpeakTextClick: (String) -> Unit = {}
) {
    var isSpeaking by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (message.isUser) {
            // User Prompt Bubble
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp, 18.dp, 2.dp, 18.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.widthIn(max = 320.dp)
                ) {
                    Text(
                        text = message.promptText,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        } else {
            // AI / System Agent Bubble
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Response text
                if (message.responseTextAr.isNotBlank()) {
                    Text(
                        text = message.responseTextAr,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Action Pills Row: Visit URL & Voice Playback
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Visit URL Pill
                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "زيارة الرابط",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = "زيارة المعاينة",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Voice TTS Speaker Button
                    OutlinedButton(
                        onClick = {
                            isSpeaking = !isSpeaking
                            onSpeakTextClick(message.responseTextAr)
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSpeaking) com.example.ui.theme.GoldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSpeaking) com.example.ui.theme.GoldPrimary.copy(alpha = 0.15f) else Color.Transparent
                        )
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Schedule else Icons.Default.Launch,
                            contentDescription = "قراءة صوتية",
                            modifier = Modifier.size(16.dp),
                            tint = if (isSpeaking) com.example.ui.theme.GoldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSpeaking) "جاري القراءة..." else "استماع صوتي",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSpeaking) com.example.ui.theme.GoldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Newly Built Card ("ما تم بناؤه من جديد")
                if (message.newlyBuiltItems.isNotEmpty()) {
                    NewlyBuiltCard(items = message.newlyBuiltItems)
                }

                // System Status Card ("الحالة الآن")
                if (message.systemStatuses.isNotEmpty()) {
                    SystemStatusCard(statuses = message.systemStatuses)
                }

                // Parallel Agents View
                if (message.activeAgents.isNotEmpty()) {
                    ParallelAgentsView(
                        agents = message.activeAgents,
                        onViewCodeClick = onViewCodeClick
                    )
                }

                // Checkpoints Timeline Collapsibles
                if (message.checkpoints.isNotEmpty()) {
                    message.checkpoints.forEach { checkpoint ->
                        CheckpointItemCard(checkpoint = checkpoint)
                    }
                }
            }
        }
    }
}

@Composable
fun CheckpointItemCard(checkpoint: com.example.data.model.CheckpointLog) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (checkpoint.durationTextAr.contains("Worked")) Icons.Default.Schedule else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (checkpoint.durationTextAr.contains("Worked")) MaterialTheme.colorScheme.onSurfaceVariant else StatusGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (checkpoint.durationTextAr.isNotBlank()) checkpoint.durationTextAr else checkpoint.checkpointTextAr,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Text(
                    text = checkpoint.summaryAr,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp, start = 24.dp)
                )
            }
        }
    }
}
