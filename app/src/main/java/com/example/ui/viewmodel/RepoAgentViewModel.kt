package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class RepoAgentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.chatDao()

    private var tts: TextToSpeech? = null

    private val _promptInput = MutableStateFlow("")
    val promptInput: StateFlow<String> = _promptInput.asStateFlow()

    private val _isPlanEnabled = MutableStateFlow(false)
    val isPlanEnabled: StateFlow<Boolean> = _isPlanEnabled.asStateFlow()

    private val _selectedModel = MutableStateFlow("الذكاء الاقتصادي")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _selectedCodeToView = MutableStateFlow<String?>(null)
    val selectedCodeToView: StateFlow<String?> = _selectedCodeToView.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // File Upload State
    private val _attachedFileName = MutableStateFlow<String?>(null)
    val attachedFileName: StateFlow<String?> = _attachedFileName.asStateFlow()

    private var _attachedFileBytes: ByteArray? = null

    // Voice System State
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    // Live Voice Call State
    private val _isLiveCallActive = MutableStateFlow(false)
    val isLiveCallActive: StateFlow<Boolean> = _isLiveCallActive.asStateFlow()

    init {
        // Initialize Android TextToSpeech for Arabic voice readout
        try {
            tts = TextToSpeech(application) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale("ar")
                }
            }
        } catch (e: Exception) {
            // Fallback if TTS not available
        }

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

    override fun onCleared() {
        super.onCleared()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {}
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

    // File Upload Handlers
    fun attachFile(fileName: String, bytes: ByteArray? = null) {
        _attachedFileName.value = fileName
        _attachedFileBytes = bytes
    }

    fun removeAttachedFile() {
        _attachedFileName.value = null
        _attachedFileBytes = null
    }

    // Voice Recording Toggle (Speech-to-Text Simulation)
    fun toggleVoiceRecording() {
        _isRecordingVoice.value = !_isRecordingVoice.value
        if (_isRecordingVoice.value) {
            viewModelScope.launch {
                delay(3000) // Simulate listening speech for 3s
                if (_isRecordingVoice.value) {
                    _promptInput.value = "قم بفحص وإصلاح ملفات المشروع وتفعيل الوكلاء الصوتيين المباشرين"
                    _isRecordingVoice.value = false
                }
            }
        }
    }

    // Live Voice Call Handlers
    fun openLiveCall() {
        _isLiveCallActive.value = true
    }

    fun closeLiveCall() {
        _isLiveCallActive.value = false
    }

    // TextToSpeech Playback
    fun speakText(text: String) {
        if (text.isBlank()) return
        try {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AgentTTS")
        } catch (e: Exception) {}
    }

    fun submitPrompt(promptText: String = _promptInput.value) {
        val currentFileName = _attachedFileName.value
        if (promptText.isBlank() && currentFileName == null) return

        var fullUserPrompt = promptText.trim()
        if (currentFileName != null) {
            fullUserPrompt += "\n[مرفق ملف: $currentFileName]"
        }

        _promptInput.value = ""
        removeAttachedFile()

        viewModelScope.launch {
            // 1. Add User Message
            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                promptText = fullUserPrompt,
                responseTextAr = "",
                isUser = true
            )
            saveMessage(userMsg)

            // 2. Prepare Initial Automatic Parallel Agents
            val initialAgents = listOf(
                AgentTask(
                    id = UUID.randomUUID().toString(),
                    type = AgentType.PLANNER,
                    titleAr = "تحليل وتخطيط معمارية العميل الذكي",
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
                    titleAr = "توليد الكود ومعالجة المرفقات الصوتية والبرمجية",
                    descriptionAr = "إصلاح app.html وتفعيل الاتصال المباشر",
                    progress = 0.1f,
                    status = AgentStatus.RUNNING
                ),
                AgentTask(
                    id = UUID.randomUUID().toString(),
                    type = AgentType.SECURITY_AUDITOR,
                    titleAr = "مراجعة الأمان والـ Diamond Economy",
                    descriptionAr = "إعادة ضبط الجلسات والموارد يومياً",
                    progress = 0.1f,
                    status = AgentStatus.RUNNING
                )
            )

            val aiMsgId = UUID.randomUUID().toString()
            val initialAiMsg = ChatMessage(
                id = aiMsgId,
                promptText = fullUserPrompt,
                responseTextAr = "جاري تشغيل الوكلاء الأوتوماتيكيين في الخلفية بالتوازي لمعالجة الطلب والمرفقات...",
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
                                // Generated Smart Fix & Audio Call Handler
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
            val geminiResponse = GeminiClient.generateAgentOrchestration(fullUserPrompt)

            // 5. Finalize AI Message with Gemini Response
            updateAiMessage(aiMsgId) { current ->
                current.copy(
                    responseTextAr = geminiResponse,
                    isAgentBuilding = false,
                    newlyBuiltItems = listOf(
                        NewlyBuiltItem("تحديث المسارات والمرفقات", "معالجة الملف المرفق وتحديث مسارات الـ API أوتوماتيكياً"),
                        NewlyBuiltItem("تحديث fetchAuth الصوتية", "تكامل النظام الصوتي والاتصال المباشر مع العميل الذكي AI"),
                        NewlyBuiltItem("تكامل uvicorn", "تشغيل uvicorn api.main:app على المنفذ 8000 بوضع Demo mode")
                    ),
                    systemStatuses = listOf(
                        SystemStatusItem("Python API", "متصل DB يعمل على :8000", isOnline = true),
                        SystemStatusItem("Express Proxy", "يعمل على :8080", isOnline = true),
                        SystemStatusItem("Direct Proxy Auth", "تسجيل الدخول متصل ومستقر", isOnline = true),
                        SystemStatusItem("النظام الصوتي المباشر", "نشِط ومفعل مع العميل الذكي", isOnline = true)
                    ),
                    checkpoints = listOf(
                        CheckpointLog("Worked for 13 minutes", "", "تم معالجة الطلب والمرفقات وإلغاء كلمة ريبو نهائياً"),
                        CheckpointLog("", "Checkpoint made 34 minutes ago", "تأكيد نقاط الاستعادة لجميع وكلاء البناء والتحليل")
                    )
                )
            }
        }
    }

    private suspend fun seedInitialState() {
        val initialMessage = ChatMessage(
            id = "initial_demo_msg",
            promptText = "تأكيد مسارات المصادقة وتفعيل العميل الذكي والنظام الصوتي المباشر",
            responseTextAr = "أهلاً بك في العميل الذكي AI!\nتم تفعيل الوكلاء المتوازيين بالكامل مع دعم رفع الملفات، النظام الصوتي، والاتصال المباشر.",
            isUser = false,
            newlyBuiltItems = listOf(
                NewlyBuiltItem("سلسلة التوجيه الكاملة", "Browser → Express :8080 ( /api/dtrn/api/* ) → Python :8000 ( /api/* )"),
                NewlyBuiltItem("النظام الصوتي المباشر", "مكالمات صوتية وقراءة نصوص فورية باللغة العربية"),
                NewlyBuiltItem("رفع الملفات الأوتوماتيكي", "إمكانية إرفاق الأكواد والمستندات بضغطة زر"),
                NewlyBuiltItem("Demo mode", "يعمل بأعلى كفاءة مع المعالجة التلقائية")
            ),
            systemStatuses = listOf(
                SystemStatusItem("Python API", "متصل DB يعمل على :8000", isOnline = true),
                SystemStatusItem("Express Proxy", "يعمل على :8080", isOnline = true),
                SystemStatusItem("الاتصال الصوتي المباشر", "نشِط وجاهز للمكالمات", isOnline = true),
                SystemStatusItem("RTL الواجهة العربية", "تظهر بالتصميم الفاخر الرخامي RTL", isOnline = true)
            ),
            checkpoints = listOf(
                CheckpointLog("Worked for 13 minutes", "", "تجميع الوكلاء وإصلاح التوجيه تلقائياً"),
                CheckpointLog("", "Checkpoint made 34 minutes ago", "تم حفظ نقطة التحقق في الموديل")
            ),
            activeAgents = listOf(
                AgentTask(
                    id = "ag1",
                    type = AgentType.PLANNER,
                    titleAr = "تحليل وتوزيع المهام مع معمارية العميل الذكي",
                    descriptionAr = "تحديد الوكلاء تلقائياً بدلاً من الاستدعاء اليدوي",
                    progress = 1.0f,
                    status = AgentStatus.COMPLETED
                ),
                AgentTask(
                    id = "ag2",
                    type = AgentType.CODE_BUILDER,
                    titleAr = "توليد كود المصادقة وتفعيل رفع الملفات الصوتية",
                    descriptionAr = "تحديث الواجهات والربط بـ Express Proxy",
                    progress = 1.0f,
                    status = AgentStatus.COMPLETED,
                    generatedCode = """
                        // Smart Agent API fix
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
            agentsJson = "",
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
