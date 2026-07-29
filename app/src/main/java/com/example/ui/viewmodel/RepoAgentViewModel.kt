package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.api.RenderPostgresSyncClient
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.UserPreferencesEntity
import com.example.data.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class RepoAgentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.chatDao()
    private val prefDao = db.userPreferencesDao()

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

    // DTR Hands-free Voice Assistant State
    private val _isDtrVoiceAssistantActive = MutableStateFlow(false)
    val isDtrVoiceAssistantActive: StateFlow<Boolean> = _isDtrVoiceAssistantActive.asStateFlow()

    // GitHub & Deploy Modal State
    private val _isGitHubModalActive = MutableStateFlow(false)
    val isGitHubModalActive: StateFlow<Boolean> = _isGitHubModalActive.asStateFlow()

    private val _githubToken = MutableStateFlow("ghp_xxxxxxxxxxxxxxxxxxxx")
    val githubToken: StateFlow<String> = _githubToken.asStateFlow()

    private val _githubUser = MutableStateFlow("omarlhlbwy441-netizen")
    val githubUser: StateFlow<String> = _githubUser.asStateFlow()

    private val _githubRepo = MutableStateFlow("dtr-n-fixed")
    val githubRepo: StateFlow<String> = _githubRepo.asStateFlow()

    private val _postgresUrl = MutableStateFlow("postgresql://dtr_user:password@dtr-db.onrender.com/dtr_database")
    val postgresUrl: StateFlow<String> = _postgresUrl.asStateFlow()

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

        // Load initial default system message or saved database messages & preferences
        viewModelScope.launch {
            // Load Preferences from Room
            val savedToken = prefDao.getValue("github_token")
            if (!savedToken.isNullOrBlank()) _githubToken.value = savedToken

            val savedUser = prefDao.getValue("github_user")
            if (!savedUser.isNullOrBlank()) _githubUser.value = savedUser

            val savedRepo = prefDao.getValue("github_repo")
            if (!savedRepo.isNullOrBlank()) _githubRepo.value = savedRepo

            val savedPgUrl = prefDao.getValue("postgres_url")
            if (!savedPgUrl.isNullOrBlank()) _postgresUrl.value = savedPgUrl

            // Load Chat Messages
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

    // DTR Hands-free Voice Assistant Handlers
    fun openDtrVoiceAssistant() {
        _isDtrVoiceAssistantActive.value = true
    }

    fun closeDtrVoiceAssistant() {
        _isDtrVoiceAssistantActive.value = false
    }

    // GitHub & Deploy Modal Handlers
    fun openGitHubModal() {
        _isGitHubModalActive.value = true
    }

    fun closeGitHubModal() {
        _isGitHubModalActive.value = false
    }

    fun saveGitHubConfig(token: String, user: String, repo: String) {
        _githubToken.value = token
        _githubUser.value = user
        _githubRepo.value = repo
        viewModelScope.launch {
            prefDao.savePreference(UserPreferencesEntity("github_token", token))
            prefDao.savePreference(UserPreferencesEntity("github_user", user))
            prefDao.savePreference(UserPreferencesEntity("github_repo", repo))
        }
    }

    fun savePostgresConfig(url: String) {
        _postgresUrl.value = url
        viewModelScope.launch {
            prefDao.savePreference(UserPreferencesEntity("postgres_url", url))
            // Perform programmatic connection test and sync
            RenderPostgresSyncClient.testConnection(url)
            val allPrefs = listOf(
                UserPreferencesEntity("github_token", _githubToken.value),
                UserPreferencesEntity("github_user", _githubUser.value),
                UserPreferencesEntity("github_repo", _githubRepo.value),
                UserPreferencesEntity("postgres_url", url)
            )
            RenderPostgresSyncClient.syncPreferences(url, allPrefs)
        }
    }

    fun wipeMemory() {
        viewModelScope.launch {
            dao.clearAll()
            seedInitialState()
        }
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
        val attachedBytes = _attachedFileBytes
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

            val lowerPrompt = fullUserPrompt.lowercase()
            val isBuildOrFixRequest = lowerPrompt.contains("بناء") || lowerPrompt.contains("تعديل") || lowerPrompt.contains("اصلاح") || lowerPrompt.contains("أنشئ") || lowerPrompt.contains("fix") || lowerPrompt.contains("build")

            // 2. Prepare Automatic Parallel Agents if building/fixing
            val initialAgents = if (isBuildOrFixRequest) {
                listOf(
                    AgentTask(
                        id = UUID.randomUUID().toString(),
                        type = AgentType.PLANNER,
                        titleAr = "تحليل وتخطيط المعمارية",
                        descriptionAr = "توزيع المهام أوتوماتيكياً",
                        progress = 0.2f,
                        status = AgentStatus.RUNNING
                    ),
                    AgentTask(
                        id = UUID.randomUUID().toString(),
                        type = AgentType.CODE_BUILDER,
                        titleAr = "توليد الكود ومعالجة المرفقات",
                        descriptionAr = "معالجة الشفرة والتعديلات",
                        progress = 0.1f,
                        status = AgentStatus.RUNNING
                    )
                )
            } else emptyList()

            val aiMsgId = UUID.randomUUID().toString()
            val initialAiMsg = ChatMessage(
                id = aiMsgId,
                promptText = fullUserPrompt,
                responseTextAr = if (isBuildOrFixRequest) "جاري تشغيل الوكلاء لمعالجة الطلب..." else "جاري تحليل الاستفسار والإجابة...",
                isUser = false,
                activeAgents = initialAgents,
                isAgentBuilding = isBuildOrFixRequest
            )
            saveMessage(initialAiMsg)

            // 3. Simulate Parallel Agent Progress if building/fixing
            if (isBuildOrFixRequest) {
                launch {
                    repeat(4) {
                        delay(400)
                        val updatedAgents = initialAgents.map { agent ->
                            val newProgress = (agent.progress + 0.25f).coerceAtMost(1.0f)
                            agent.copy(
                                progress = newProgress,
                                status = if (newProgress >= 1.0f) AgentStatus.COMPLETED else AgentStatus.RUNNING
                            )
                        }
                        updateAiMessage(aiMsgId) { current ->
                            current.copy(activeAgents = updatedAgents)
                        }
                    }
                }
            }

            // 4. Fetch Actual Response from Gemini API with image bytes and conversation history for Long-Term Memory
            val currentHistory = _messages.value
            val geminiResponse = GeminiClient.generateAgentOrchestration(
                prompt = fullUserPrompt,
                imageBytes = attachedBytes,
                history = currentHistory
            )

            // 5. Finalize AI Message with Gemini Response
            updateAiMessage(aiMsgId) { current ->
                current.copy(
                    responseTextAr = geminiResponse,
                    isAgentBuilding = false,
                    newlyBuiltItems = if (isBuildOrFixRequest) listOf(
                        NewlyBuiltItem("تنفيذ وتطوير الأكواد", "تمت معالجة الطلب بالكامل وتحديث المكونات بنجاح")
                    ) else emptyList(),
                    systemStatuses = emptyList(),
                    checkpoints = emptyList()
                )
            }
        }
    }

    private suspend fun seedInitialState() {
        val initialMessage = ChatMessage(
            id = "initial_demo_msg",
            promptText = "بدء الجلسة - وحش البرمجة k1.0",
            responseTextAr = "أهلاً بك في واجهة وحشك البرمجي! أنا رفيقك التقني الهجين المعروف بوحش البرمجة، بماذا أساعدك اليوم وما هي خططك للمشاريع؟\n\nأنا نظام وحش البرمجة k1.0 الهجين المزود بذاكرة طويلة المدى، معالجة المرفقات والصور، الوكلاء المتوازيين، وخاصية المعاينة الحية والتنفيذ المباشر للأكواد.",
            isUser = false,
            newlyBuiltItems = listOf(
                NewlyBuiltItem("ذاكرة طويلة المدى مستمرة", "حفظ وتذكر جميع المحادثات أوتوماتيكياً في قاعدة بيانات Room المحفوظة"),
                NewlyBuiltItem("المحرك الهجين k1.0", "استجابة فورية وتفاعل ذكي وتنفيذ الأوامر البرمجية الكاملة"),
                NewlyBuiltItem("شاشة العرض والمعاينة الحية 📺", "معاينة الواجهات، المواقع، الألعاب، والفيديوهات المباشرة"),
                NewlyBuiltItem("النظام الصوتي المباشر 🎙️", "مكالمات صوتية وقراءة نصوص فورية باللغة العربية")
            ),
            systemStatuses = listOf(
                SystemStatusItem("ذاكرة Room المحلية", "نشطة وتتذكر كافة المحادثات", isOnline = true),
                SystemStatusItem("وحش البرمجة k1.0", "متصل وجاهز لتنفيذ الأوامر", isOnline = true),
                SystemStatusItem("الوكلاء المتوازيون", "جاهزون للتخطيط والتوليد والتنفيذ", isOnline = true)
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
        val entity = toEntity(msg)
        dao.insertMessage(entity)
        if (_postgresUrl.value.isNotBlank()) {
            RenderPostgresSyncClient.syncChatMessage(_postgresUrl.value, entity)
        }
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
