package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val promptText: String,
    val responseTextAr: String,
    val timestamp: Long,
    val isUser: Boolean,
    val agentsJson: String,
    val newlyBuiltJson: String,
    val systemStatusJson: String,
    val checkpointsJson: String
)
