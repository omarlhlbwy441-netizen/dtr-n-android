package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusYellow

enum class AgentType(val displayNameAr: String, val iconResName: String, val badgeColor: Long) {
    PLANNER("وكيل التخطيط والمعمارية", "ic_planner", 0xFF8B5CF6),
    CODE_BUILDER("وكيل البناء والبرمجة", "ic_code", 0xFF3B82F6),
    BACKEND_SERVICE("وكيل الخدمات والـ Proxy", "ic_server", 0xFF10B981),
    SECURITY_AUDITOR("وكيل الفحص والأمان", "ic_shield", 0xFFEAB308)
}

enum class AgentStatus(val labelAr: String) {
    IDLE("في الانتظار"),
    RUNNING("جاري التشغيل بالتوازي..."),
    COMPLETED("مكتمل 🟢"),
    FAILED("خطأ 🔴")
}

data class AgentTask(
    val id: String,
    val type: AgentType,
    val titleAr: String,
    val descriptionAr: String,
    val progress: Float = 0f,
    val status: AgentStatus = AgentStatus.IDLE,
    val logs: List<String> = emptyList(),
    val generatedCode: String? = null
)

data class SystemStatusItem(
    val titleAr: String,
    val statusTextAr: String,
    val isOnline: Boolean = true,
    val portInfo: String? = null
)

data class NewlyBuiltItem(
    val titleAr: String,
    val detailAr: String
)

data class CheckpointLog(
    val durationTextAr: String, // e.g. "Worked for 13 minutes"
    val checkpointTextAr: String, // e.g. "Checkpoint made 34 minutes ago"
    val summaryAr: String
)

data class ChatMessage(
    val id: String,
    val promptText: String,
    val responseTextAr: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isUser: Boolean = false,
    val activeAgents: List<AgentTask> = emptyList(),
    val newlyBuiltItems: List<NewlyBuiltItem> = emptyList(),
    val systemStatuses: List<SystemStatusItem> = emptyList(),
    val checkpoints: List<CheckpointLog> = emptyList(),
    val isAgentBuilding: Boolean = false
)
