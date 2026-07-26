package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class RepoAgentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.chatDao()

    private val _promptInput = MutableStateFlow("")
    val promptInput: StateFlow<String> = _promptInput.asStateFlow()

    private val _isPlanEnabled = MutableStateFlow(false)
    val isPlanEnabled: StateFlow<Boolean> = _isPlanEnabled.asStateFlow()

    private val _selectedModel = MutableStateFlow("Economy")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _selectedCodeToView = MutableStateFlow<String?>(null)
    val selectedCodeToView: StateFlow<String?> = _selectedCodeToView.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    init {
        // Load initial default system message or saved database messages
        viewModelScope.launch {
            dao.getAllMessages().collect { entities ->
                if (entities.isEmpty()) {
                    seedInitialState()
                } else {
                    _messages.value = entities.map { parseEntity(it) }
                }
            }
        }
    }

    fun onPromptChange(newText: String) {
        _promptInput.value = newText
    }

    fun togglePlan() {
        _isPlanEnabled.value = !_isPlanEnabled.value
    }

    fun selectModel(model: String) {
        _selectedModel.value = model
    }

    fun viewCode(code: String) {
        _selectedCodeToView.value = code
    }

    fun dismissCodeViewer() {
        _selectedCodeToView.value = null
    }

    fun submitPrompt(promptText: String = _promptInput.value) {
        if (promptText.isBlank()) return

        val userPrompt = promptText.trim()
        _promptInput.value = ""

        viewModelScope.launch {
            // 1. Add User Message
            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                promptText = userPrompt,
                responseTextAr = "",
                isUser = true
            )
            saveMessage(userMsg)

            // 2. Prepare Initial Automatic Parallel Agents
            val initialAgents = listOf(
                AgentTask(
                    id = UUID.randomUUID().toString(),
                    type = AgentType.PLANNER,
                    titleAr = "تحليل متطلبات المشهد وتخطيط البنية",
                    descriptionAr = "توزيع المهام أوتوماتيكياً على الخدمات والمُوجِّهات",
                    progress = 0.2f,
                    status = AgentStatus.RUNNING
                ),
                AgentTask(
                    id = UUID.randomUUID().toString(),
                    type = AgentType.BACKEND_SERVICE,
                    titleAr = "إعداد Express Proxy وتوصيل Python API :8000",
                    descriptionAr = "فحص المسارات /api/dtrn/api/* والجلسات",
                    progress = 0.1f,
                    status = AgentStatus.RUNNING
                ),
                AgentTask(
                    id = UUID.randomUUID().toString(),
                    type = AgentType.CODE_BUILDER,
                    titleAr = "توليد كود المصادقة وتعديل API_BASE",
                    descriptionAr = "إصلاح app.html والـ fetchAuth المباشر",
                    progress = 0.1f,
                    status = AgentStatus.RUNNING
                ),
                AgentTask(
                    id = UUID.randomUUID().toString(),
                    type = AgentType.SECURITY_AUDITOR,
                    titleAr = "مراجعة الأمان وتقييد المعدلات والـ Diamond Economy",
                    descriptionAr = "إعادة ضبط الجلسات والموارد يومياً",
                    progress = 0.1f,
                    status = AgentStatus.RUNNING
                )
            )

            val aiMsgId = UUID.randomUUID().toString()
            val initialAiMsg = ChatMessage(
                id = aiMsgId,
                promptText = userPrompt,
                responseTextAr = "جاري تشغيل الوكلاء في الخلفية بالتوازي لمعالجة الطلب...",
                isUser = false,
                activeAgents = initialAgents,
                isAgentBuilding = true
            )
            saveMessage(initialAiMsg)

            // 3. Simulate Parallel Agent Steps & Progress
            launch {
                repeat(4) { step ->
                    delay(500)
                    val updatedAgents = initialAgents.map { agent ->
                        val newProgress = (agent.progress + 0.25f).coerceAtMost(1.0f)
                        val isDone = newProgress >= 1.0f
                        agent.copy(
                            progress = newProgress,
                            status = if (isDone) AgentStatus.COMPLETED else AgentStatus.RUNNING,
                            generatedCode = if (isDone && agent.type == AgentType.CODE_BUILDER) {
                                """
                                // Generated Fix for app.html login & fetchAuth
                                const API_BASE = 'https://dtr-no.onrender.com/api/dtrn/api';
                                
                                async function fetchAuth(endpoint, options = {}) {
                                  const token = localStorage.getItem('dtr_session_token');
                                  const headers = {
                                    'Content-Type': 'application/json',
                                    ...(token ? { 'Authorization': `Bearer ${'$'}{token}` } : {})
                                  };
                                  return fetch(`${'$'}{API_BASE}${'$'}{endpoint}`, { ...options, headers });
                                }
                                """.trimIndent()
                            } else null
                        )
                    }

                    updateAiMessage(aiMsgId) { current ->
                        current.copy(activeAgents = updatedAgents)
                    }
                }
            }

            // 4. Fetch Actual Response from Gemini API
            val geminiResponse = GeminiClient.generateAgentOrchestration(userPrompt)

            // 5. Finalize AI Message with Gemini Response, Newly Built List, and Status
            updateAiMessage(aiMsgId) { current ->
                current.copy(
                    responseTextAr = geminiResponse,
                    isAgentBuilding = false,
                    newlyBuiltItems = listOf(
                        NewlyBuiltItem("تعديل مسار API_BASE", "تعديل app.html ليصل إلى /api/dtrn/api/* عبر Express Proxy :8080"),
                        NewlyBuiltItem("تحديث fetchAuth", "تمرير التوكن تلقائياً وإعادة المحاولة أوتوماتيكياً عند انقطاع الاتصال"),
                        NewlyBuiltItem("تكامل uvicorn", "تشغيل uvicorn api.main:app على المنفذ 8000 بوضع Demo mode")
                    ),
                    systemStatuses = listOf(
                        SystemStatusItem("Python API", "متصل DB يعمل على :8000", isOnline = true),
                        SystemStatusItem("Express Proxy", "يعمل على :8080", isOnline = true),
                        SystemStatusItem("Direct Proxy Auth", "تسجيل الدخول متصل ومستقر", isOnline = true),
                        SystemStatusItem("RTL Luxury Interface", "تظهر بالتصميم الأسود/الذهبي الرخامي مع RTL", isOnline = true)
                    ),
                    checkpoints = listOf(
                        CheckpointLog("Worked for 13 minutes", "", "تم إنشاء 3 إصلاحات وتوليد كود المصادقة المباشر"),
                        CheckpointLog("", "Checkpoint made 34 minutes ago", "تأكيد نقاط الاستعادة لجميع وكلاء البناء والتحليل")
                    )
                )
            }
        }
    }

    private suspend fun seedInitialState() {
        val initialMessage = ChatMessage(
            id = "initial_demo_msg",
            promptText = "Fix API_BASE and fallback fetchAuth in app.html for login",
            responseTextAr = "Fix API_BASE and fallback fetchAuth in app.html for login\nDeployed on July 24, 2026 at 5:23:57 AM GMT+3",
            isUser = false,
            newlyBuiltItems = listOf(
                NewlyBuiltItem("سلسلة التوجيه الكاملة", "Browser → Express :8080 ( /api/dtrn/api/* ) → Python :8000 ( /api/* )"),
                NewlyBuiltItem("Python workflow", "يشغّل uvicorn api.main:app على منفذ 8000"),
                NewlyBuiltItem("auto-migration + seed admin", "عند الإقلاع: admin@dtr-n.com / dtrn2026"),
                NewlyBuiltItem("نظام الموارد", "(sessions/steps/diamonds/code_generations) مع reset يومي"),
                NewlyBuiltItem("Demo mode", "يعمل بدون قاعدة بيانات بدلاً من الانهيار")
            ),
            systemStatuses = listOf(
                SystemStatusItem("Python API", "متصل DB يعمل على :8000", isOnline = true),
                SystemStatusItem("Express Proxy", "يعمل على :8080", isOnline = true),
                SystemStatusItem("Direct Proxy", "ومباشرة proxy تسجيل الدخول: يعمل عبر الـ", isOnline = true),
                SystemStatusItem("RTL الواجهة", "تظهر بالتصميم الأسود/الذهبي الرخامي مع RTL", isOnline = true)
            ),
            checkpoints = listOf(
                CheckpointLog("Worked for 13 minutes", "", "تجميع الوكلاء وإصلاح التوجيه تلقائياً"),
                CheckpointLog("", "Checkpoint made 34 minutes ago", "تم حفظ نقطة التحقق في الموديل")
            ),
            activeAgents = listOf(
                AgentTask(
                    id = "ag1",
                    type = AgentType.PLANNER,
                    titleAr = "تحليل وتوزيع المهام مع المعمارية",
                    descriptionAr = "تحديد الوكلاء تلقائياً بدلاً من الاستدعاء اليدوي",
                    progress = 1.0f,
                    status = AgentStatus.COMPLETED
                ),
                AgentTask(
                    id = "ag2",
                    type = AgentType.CODE_BUILDER,
                    titleAr = "توليد كود المصادقة والـ API_BASE",
                    descriptionAr = "إصلاح app.html والربط بـ Express Proxy",
                    progress = 1.0f,
                    status = AgentStatus.COMPLETED,
                    generatedCode = """
                        // app.html API fix
                        const API_BASE = 'https://dtr-no.onrender.com/api/dtrn/api';
                        async function fetchAuth(endpoint, options = {}) {
                          const token = localStorage.getItem('token');
                          return fetch(API_BASE + endpoint, {
                            ...options,
                            headers: { 'Authorization': `Bearer ` + token, ...options.headers }
                          });
                        }
                    """.trimIndent()
                )
            )
        )
        saveMessage(initialMessage)
    }

    private suspend fun saveMessage(msg: ChatMessage) {
        val currentList = _messages.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == msg.id }
        if (index >= 0) {
            currentList[index] = msg
        } else {
            currentList.add(msg)
        }
        _messages.value = currentList
        dao.insertMessage(toEntity(msg))
    }

    private suspend fun updateAiMessage(id: String, update: (ChatMessage) -> ChatMessage) {
        val currentList = _messages.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index >= 0) {
            val updated = update(currentList[index])
            currentList[index] = updated
            _messages.value = currentList
            dao.insertMessage(toEntity(updated))
        }
    }

    private fun toEntity(msg: ChatMessage): ChatMessageEntity {
        return ChatMessageEntity(
            id = msg.id,
            promptText = msg.promptText,
            responseTextAr = msg.responseTextAr,
            timestamp = msg.timestamp,
            isUser = msg.isUser,
            agentsJson = "", // simplified for Room memory/cache
            newlyBuiltJson = "",
            systemStatusJson = "",
            checkpointsJson = ""
        )
    }

    private fun parseEntity(entity: ChatMessageEntity): ChatMessage {
        return ChatMessage(
            id = entity.id,
            promptText = entity.promptText,
            responseTextAr = entity.responseTextAr,
            timestamp = entity.timestamp,
            isUser = entity.isUser
        )
    }
}
