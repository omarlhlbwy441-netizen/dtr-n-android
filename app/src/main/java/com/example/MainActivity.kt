package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopHeaderBar(
                workspaceName = "dtr-no",
                projectName = "العميل الذكي - dtr-n-fixed",
                branchName = "الفرع الرئيسي",
                liveUrl = "https://dtr-no.onrender.com",
                onLiveCallClick = { viewModel.openLiveCall() }
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
                onModelSelect = { viewModel.selectModel(it) }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
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
    }
}
