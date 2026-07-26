package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.RepoAgentTheme
import com.example.ui.viewmodel.RepoAgentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepoAgentTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    RepoAgentApp()
                }
            }
        }
    }
}

@Composable
fun RepoAgentApp(
    viewModel: RepoAgentViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val promptInput by viewModel.promptInput.collectAsState()
    val isPlanEnabled by viewModel.isPlanEnabled.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val selectedCodeToView by viewModel.selectedCodeToView.collectAsState()

    val listState = rememberLazyListState()

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
                repoName = "omarlhlbwy441-netizen / dtr-n-fixed",
                branchName = "main",
                liveUrl = "https://dtr-no.onrender.com"
            )
        },
        bottomBar = {
            InputBottomBar(
                promptText = promptInput,
                onPromptChange = { viewModel.onPromptChange(it) },
                onSendClick = { viewModel.submitPrompt() },
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
    }
}
