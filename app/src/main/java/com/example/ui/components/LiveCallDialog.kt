package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MicOff
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OnyxDark
import kotlinx.coroutines.delay

@Composable
fun LiveCallDialog(
    onDismiss: () -> Unit,
    onSendMessageInCall: (String) -> Unit = {}
) {
    var callSeconds by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var callStatusText by remember { mutableStateOf("متصل الآن بالعميل الذكي AI...") }
    var isAiSpeaking by remember { mutableStateOf(false) }

    // Live Call Timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSeconds++
            if (callSeconds % 6 == 0) {
                isAiSpeaking = !isAiSpeaking
                callStatusText = if (isAiSpeaking) "العميل الذكي يجيب صوتياً..." else "جاري الاستماع لصوتك..."
            }
        }
    }

    // Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF030712),
                            Color(0xFF1E1B4B)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Bar inside Call
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "المكالمة الصوتية المباشرة",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            val minutes = callSeconds / 60
                            val seconds = callSeconds % 60
                            Text(
                                text = String.format("%02d:%02d", minutes, seconds),
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(onClick = { isSpeakerOn = !isSpeakerOn }) {
                        Icon(
                            imageVector = if (isSpeakerOn) Icons.Outlined.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Speaker",
                            tint = if (isSpeakerOn) GoldPrimary else Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Avatar & Pulsing Wave Center
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 20.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(220.dp)
                    ) {
                        // Outer Pulsing Circle
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(
                                    if (isAiSpeaking) GoldPrimary.copy(alpha = 0.15f) else Color(
                                        0xFF6366F1
                                    ).copy(alpha = 0.15f)
                                )
                        )

                        // Middle Circle
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isAiSpeaking) GoldPrimary.copy(alpha = 0.3f) else Color(
                                        0xFF6366F1
                                    ).copy(alpha = 0.3f)
                                )
                        )

                        // Core Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(GoldPrimary, Color(0xFF8B5CF6))
                                    )
                                )
                                .border(3.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI Agent Avatar",
                                tint = OnyxDark,
                                modifier = Modifier.size(54.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "العميل الذكي AI",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = callStatusText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isAiSpeaking) GoldPrimary else Color(0xFF818CF8)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated Voice Wave Visualizer
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .height(36.dp)
                            .padding(horizontal = 24.dp)
                    ) {
                        val barHeights = listOf(14.dp, 28.dp, 20.dp, 36.dp, 24.dp, 30.dp, 16.dp, 32.dp, 18.dp)
                        barHeights.forEachIndexed { index, defaultHeight ->
                            val currentHeight = if (isAiSpeaking || !isMuted) {
                                (defaultHeight.value * (0.6f + (index % 3) * 0.2f)).dp
                            } else 6.dp

                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(currentHeight)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (isAiSpeaking) GoldPrimary else Color(0xFF818CF8))
                            )
                        }
                    }
                }

                // Live Subtitles / Speech Transcript Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مباشر من المحادثة الصوتية",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isAiSpeaking)
                                "» أهلاً بك! أنا أعمل في الخلفية لتنفيذ كافة المهام والتعديلات المطلوبة في مشروعك أوتوماتيكياً."
                            else
                                "» تحدث الآن... صوتك يصل للعميل الذكي مباشرة للتحليل والمعالجة.",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    }
                }

                // Call Controls Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) Color(0xFFEF4444) else Color.White.copy(alpha = 0.15f))
                            .clickable { isMuted = !isMuted },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Outlined.MicOff else Icons.Outlined.Mic,
                            contentDescription = "Mute Microphone",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // End Call Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDC2626))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // AI Agent Capabilities / Settings
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable {
                                onSendMessageInCall("توضيح المهام الحالية")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Action",
                            tint = GoldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
