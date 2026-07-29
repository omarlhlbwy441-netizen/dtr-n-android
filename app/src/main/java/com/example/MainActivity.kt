package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.SmartAgentTheme
import com.example.ui.viewmodel.RepoAgentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartAgentTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    SmartAgentApp()
                }
            }
        }
    }
}

@Composable
fun SmartAgentApp(
    viewModel: RepoAgentViewModel = viewModel()
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val promptInput by viewModel.promptInput.collectAsState()
    val isPlanEnabled by viewModel.isPlanEnabled.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val selectedCodeToView by viewModel.selectedCodeToView.collectAsState()

    val attachedFileName by viewModel.attachedFileName.collectAsState()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
    val isLiveCallActive by viewModel.isLiveCallActive.collectAsState()
    val isDtrVoiceAssistantActive by viewModel.isDtrVoiceAssistantActive.collectAsState()
    val isGitHubModalActive by viewModel.isGitHubModalActive.collectAsState()

    val githubToken by viewModel.githubToken.collectAsState()
    val githubUser by viewModel.githubUser.collectAsState()
    val githubRepo by viewModel.githubRepo.collectAsState()
    val postgresUrl by viewModel.postgresUrl.collectAsState()

    val listState = rememberLazyListState()

    // Android File Picker Launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fileUri ->
            val fileName = fileUri.lastPathSegment?.substringAfterLast('/') ?: "file_attached.txt"
            val bytes = try {
                context.contentResolver.openInputStream(fileUri)?.use { it.readBytes() }
            } catch (e: Exception) { null }
            viewModel.attachFile(fileName, bytes)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    var isWebCanvasVisible by remember { mutableStateOf(false) }
    var isSystemsPanelVisible by remember { mutableStateOf(false) }
    var isMoreOptionsVisible by remember { mutableStateOf(false) }
    var isLivePreviewSheetVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopHeaderBar(
                projectName = "وحش البرمجة k2.5 Neural"
            )
        },
        bottomBar = {
            InputBottomBar(
                promptText = promptInput,
                onPromptChange = { viewModel.onPromptChange(it) },
                onSendClick = { viewModel.submitPrompt() },
                attachedFileName = attachedFileName,
                onAttachFileClick = { filePickerLauncher.launch("*/*") },
                onRemoveFileClick = { viewModel.removeAttachedFile() },
                isRecordingVoice = isRecordingVoice,
                onVoiceRecordClick = { viewModel.toggleVoiceRecording() },
                isPlanEnabled = isPlanEnabled,
                onPlanToggle = { viewModel.togglePlan() },
                selectedModel = selectedModel,
                onModelSelect = { viewModel.selectModel(it) },
                onMoreOptionsClick = { isMoreOptionsVisible = true },
                onOpenPreviewDisplayClick = { isLivePreviewSheetVisible = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Background Web Canvas Instance (Kept rendered at 0dp height when collapsed to run web tasks in background)
            if (isWebCanvasVisible) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    HybridWebContainerView(
                        initialUrl = "https://dtr-no.onrender.com",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // Invisible background container so WebView stays loaded and processing tasks in background
                Box(modifier = Modifier.size(1.dp)) {
                    HybridWebContainerView(
                        initialUrl = "https://dtr-no.onrender.com",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Main Pure Communication Chat View
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = messages,
                    key = { it.id }
                ) { message ->
                    ChatMessageItem(
                        message = message,
                        onViewCodeClick = { code ->
                            viewModel.viewCode(code)
                        },
                        onSpeakTextClick = { text ->
                            viewModel.speakText(text)
                        }
                    )
                }
            }
        }

        // Systems & Capabilities Modal Dialog (On-demand inspection)
        if (isSystemsPanelVisible) {
            AlertDialog(
                onDismissRequest = { isSystemsPanelVisible = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "أنظمة ومقدرات الوحش البرمجي",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp)
                    ) {
                        HybridSystemsControlCenter(
                            onOpenVoiceCall = {
                                isSystemsPanelVisible = false
                                viewModel.openLiveCall()
                            },
                            onOpenDtrVoiceAssistant = {
                                isSystemsPanelVisible = false
                                viewModel.openDtrVoiceAssistant()
                            },
                            onOpenGitHubSettings = {
                                isSystemsPanelVisible = false
                                viewModel.openGitHubModal()
                            },
                            onSwitchToWebView = {
                                isSystemsPanelVisible = false
                                isWebCanvasVisible = true
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { isSystemsPanelVisible = false }) {
                        Text("إغلاق", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Code Dialog Viewer Modal
        selectedCodeToView?.let { code ->
            CodeViewerDialog(
                codeContent = code,
                onDismiss = { viewModel.dismissCodeViewer() }
            )
        }

        // Live Voice Call Dialog Modal
        if (isLiveCallActive) {
            LiveCallDialog(
                onDismiss = { viewModel.closeLiveCall() },
                onSendMessageInCall = { text ->
                    viewModel.submitPrompt(text)
                }
            )
        }

        // DTR Hands-free Voice Assistant Dialog Modal
        if (isDtrVoiceAssistantActive) {
            DtrVoiceAssistantDialog(
                onDismiss = { viewModel.closeDtrVoiceAssistant() },
                onExecuteAction = { text ->
                    viewModel.submitPrompt(text)
                }
            )
        }

        // GitHub & Deploy Settings Modal
        if (isGitHubModalActive) {
            GitHubAndDeployModal(
                githubToken = githubToken,
                githubUser = githubUser,
                githubRepo = githubRepo,
                postgresUrl = postgresUrl,
                onSaveGitHubConfig = { token, user, repo ->
                    viewModel.saveGitHubConfig(token, user, repo)
                },
                onSavePostgresConfig = { pgUrl ->
                    viewModel.savePostgresConfig(pgUrl)
                },
                onWipeMemoryClick = {
                    viewModel.wipeMemory()
                },
                onOpenDtrVoiceAssistant = {
                    viewModel.openDtrVoiceAssistant()
                },
                onDismiss = { viewModel.closeGitHubModal() }
            )
        }

        // Three Dots User Profile & Options Modal
        if (isMoreOptionsVisible) {
            UserProfileAndOptionsMenu(
                userName = "مصطفى",
                userEmail = "wolfforleatherproducts@gmail.com",
                currentProjectName = "وحش البرمجة",
                workspaceName = "dtr-no",
                branchName = "الفرع الرئيسي",
                isWebCanvasVisible = isWebCanvasVisible,
                onToggleWebCanvas = { isWebCanvasVisible = !isWebCanvasVisible },
                onOpenSystemsPanel = { isSystemsPanelVisible = true },
                onOpenLiveCall = { viewModel.openLiveCall() },
                onOpenGitHubSettings = { viewModel.openGitHubModal() },
                onDismiss = { isMoreOptionsVisible = false }
            )
        }

        // Live Preview Display Screen Sheet Modal
        if (isLivePreviewSheetVisible) {
            LivePreviewDisplaySheet(
                onDismiss = { isLivePreviewSheetVisible = false },
                onViewCode = { code ->
                    isLivePreviewSheetVisible = false
                    viewModel.viewCode(code)
                }
            )
        }
    }
}
