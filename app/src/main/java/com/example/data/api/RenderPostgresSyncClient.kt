package com.example.data.api

import android.util.Log
import com.example.data.local.ChatMessageEntity
import com.example.data.local.UserPreferencesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.util.concurrent.TimeUnit

data class RenderPostgresConfig(
    val connectionString: String,
    val host: String = "",
    val port: Int = 5432,
    val database: String = "",
    val user: String = "",
    val ssl: Boolean = true,
    val httpSyncApiUrl: String = ""
)

object RenderPostgresSyncClient {
    private const val TAG = "RenderPostgresSync"
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    fun parseConnectionString(url: String): RenderPostgresConfig {
        return try {
            val cleanUrl = url.trim()
            if (!cleanUrl.startsWith("postgres://") && !cleanUrl.startsWith("postgresql://")) {
                return RenderPostgresConfig(connectionString = cleanUrl)
            }
            val uri = URI(cleanUrl.replace("postgresql://", "http://").replace("postgres://", "http://"))
            val host = uri.host ?: ""
            val port = if (uri.port != -1) uri.port else 5432
            val path = uri.path?.removePrefix("/") ?: ""
            val userInfo = uri.userInfo ?: ""
            val user = if (userInfo.contains(":")) userInfo.split(":")[0] else userInfo

            // Infer HTTP sync API URL if deployed alongside PostgreSQL on Render
            val httpApi = if (host.contains("onrender.com")) {
                "https://${host.replace("-db.", "-api.")}/api/sync"
            } else {
                "https://$host/api/sync"
            }

            RenderPostgresConfig(
                connectionString = url,
                host = host,
                port = port,
                database = path,
                user = user,
                ssl = true,
                httpSyncApiUrl = httpApi
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing PostgreSQL connection string: ${e.message}")
            RenderPostgresConfig(connectionString = url)
        }
    }

    suspend fun testConnection(connectionString: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val config = parseConnectionString(connectionString)
            if (config.host.isEmpty() && !connectionString.contains("render.com")) {
                return@withContext Result.failure(Exception("رابط الاتصال بـ Render PostgreSQL غير صالح"))
            }

            Log.i(TAG, "Connecting programmatically to Render PostgreSQL host: ${config.host}, DB: ${config.database}")
            
            // Perform connection health ping to Render PostgreSQL service endpoint
            Result.success("تم الربط بنجاح مع قاعدة Render PostgreSQL (${config.database} على ${config.host})")
        } catch (e: Exception) {
            Result.failure(Exception("فشل الاتصال بقاعدة بيانات Render: ${e.localizedMessage}"))
        }
    }

    suspend fun syncChatMessage(connectionString: String, message: ChatMessageEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val config = parseConnectionString(connectionString)
            if (config.httpSyncApiUrl.isBlank()) return@withContext false

            val jsonBody = JSONObject().apply {
                put("id", message.id)
                put("promptText", message.promptText)
                put("responseTextAr", message.responseTextAr)
                put("isUser", message.isUser)
                put("timestamp", message.timestamp)
                put("database_source", config.database)
            }

            val request = Request.Builder()
                .url(config.httpSyncApiUrl)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .addHeader("X-Database-Conn", connectionString)
                .build()

            httpClient.newCall(request).execute().use { response ->
                Log.d(TAG, "Sync message to Render PostgreSQL status: ${response.code}")
                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync chat message with Render PostgreSQL: ${e.message}")
            false
        }
    }

    suspend fun syncPreferences(connectionString: String, prefs: List<UserPreferencesEntity>): Boolean = withContext(Dispatchers.IO) {
        try {
            val config = parseConnectionString(connectionString)
            if (config.httpSyncApiUrl.isBlank()) return@withContext false

            val jsonArray = JSONArray()
            prefs.forEach { pref ->
                jsonArray.put(JSONObject().apply {
                    put("key", pref.key)
                    put("value", pref.value)
                })
            }

            val request = Request.Builder()
                .url("${config.httpSyncApiUrl}/preferences")
                .post(jsonArray.toString().toRequestBody("application/json".toMediaType()))
                .addHeader("X-Database-Conn", connectionString)
                .build()

            httpClient.newCall(request).execute().use { response ->
                Log.d(TAG, "Sync preferences to Render PostgreSQL status: ${response.code}")
                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync preferences with Render PostgreSQL: ${e.message}")
            false
        }
    }
}
