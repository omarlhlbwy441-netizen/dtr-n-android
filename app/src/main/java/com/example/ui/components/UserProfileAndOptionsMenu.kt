package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg

data class UserProject(
    val id: String,
    val name: String,
    val description: String,
    val isCurrent: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileAndOptionsMenu(
    userName: String = "مصطفى",
    userEmail: String = "wolfforleatherproducts@gmail.com",
    currentProjectName: String = "الوحش البرمجي",
    workspaceName: String = "dtr-no",
    branchName: String = "الفرع الرئيسي",
    isWebCanvasVisible: Boolean = false,
    onToggleWebCanvas: () -> Unit,
    onOpenSystemsPanel: () -> Unit,
    onOpenLiveCall: () -> Unit,
    onOpenGitHubSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    val userProjects = listOf(
        UserProject("p1", "وحش البرمجة k2.5", "تطبيق الذكاء الاصطناعي الأسرع - المحرك التفاعلي k2.5", isCurrent = true),
        UserProject("p2", "dtr-n-android", "مستودع أندرويد الهجين الشامل", isCurrent = false),
        UserProject("p3", "dtr-n-fixed", "مستودع المزامنة والبناء المستقر", isCurrent = false),
        UserProject("p4", "محلل الأكواد الذكي Kimi AI Engine", "معالج الأكواد التلقائي والذاكرة سريعة الاستجابة", isCurrent = false)
    )

    var selectedPlan by remember { mutableStateOf("خطة وحش البرمجة (VIP Ultra k2.5)") }
    var showUpgradeDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            // Drag handle header title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "وحش البرمجة k1.0 ⚡",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "الإصدار k1.0 - النسخة الأولى بجميع الموارد والأنظمة المفتوحة",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. USER PROFILE SECTION
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // User Avatar
                            Surface(
                                shape = CircleShape,
                                color = GoldPrimary,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = userName.take(1),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = userEmail,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = StatusGreenBg,
                                        border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = "نشط • المطور المعتمد ⚡",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. UPGRADE PLAN SECTION
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "الخطة",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedPlan,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoldPrimary,
                                    modifier = Modifier.clickable { showUpgradeDialog = true }
                                ) {
                                    Text(
                                        text = "ترقية الخطة",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "تتضمن: معالجة غير محدودة بالذكاء الاصطناعي، محرك الألعاب والفيزياء، المزامنة الفورية لـ GitHub والمكالمات الصوتية المباشرة.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 3. USER PROJECTS SECTION
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مشاريع المستخدم 📁",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "+ مشروع جديد",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        userProjects.forEach { project ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (project.isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(
                                    1.dp,
                                    if (project.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = project.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (project.isCurrent) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = MaterialTheme.colorScheme.primary
                                                ) {
                                                    Text(
                                                        text = "نشِط",
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onPrimary,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = project.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "فتح المشروع",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. WORKSPACE & MOVED TOP-DETAILS SECTION
                item {
                    Column {
                        Text(
                            text = "تفاصيل البيئة والمساحة 🛠",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("مساحة العمل (Workspace):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(workspaceName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("الفرع الحالي (Branch):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(branchName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("حالة معالج الخلفية:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("الخلفية تعمل بنجاح ⚡", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                                }
                            }
                        }
                    }
                }

                // 5. CLOUD INTEGRATIONS & SEARCH SYSTEMS SECTION
                item {
                    Column {
                        Text(
                            text = "أنظمة الربط والتكاملات السحابية 🌐",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Google Drive integration
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDismiss()
                                            onOpenSystemsPanel()
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Column {
                                            Text("Google Drive", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("مزامنة المستندات والأكواد مجاناً", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text("نشِط ومتصل ✅", fontSize = 11.sp, color = StatusGreen, fontWeight = FontWeight.Bold)
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                // GitHub Integration
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDismiss()
                                            onOpenGitHubSettings()
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Column {
                                            Text("حساب GitHub والمستودعات", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("رفع الشفرات وبناء APK تلقائياً", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text("إدارة الربط ⚙️", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                // Cloud Hosting Platforms
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDismiss()
                                            onOpenSystemsPanel()
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Column {
                                            Text("منصات الاستضافة (Render / Vercel)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("تتبع الخوادم والنشر المباشر", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text("نشِط 🟢", fontSize = 11.sp, color = StatusGreen, fontWeight = FontWeight.Bold)
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                // Live Web & Academic Libraries Search Engine
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDismiss()
                                            onOpenSystemsPanel()
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Column {
                                            Text("محرك البحث بالنت والكتب والمكتبات", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Google Books, arXiv, PubMed, IEEE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text("بحث مباشر 🔍", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 6. QUICK ACTIONS TILES
                item {
                    Column {
                        Text(
                            text = "الأدوات والأنظمة ⚡",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Live Call Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF15803D),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onDismiss()
                                        onOpenLiveCall()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("مكالمة حية", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            // Toggle Web View
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isWebCanvasVisible) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onToggleWebCanvas()
                                        onDismiss()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isWebCanvasVisible) "إخفاء الويب" else "معاينة الويب", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Systems Panel
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onDismiss()
                                        onOpenSystemsPanel()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Hub, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("المقدرات والأنظمة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // GitHub Settings
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldPrimary,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onDismiss()
                                        onOpenGitHubSettings()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("GitHub والإعدادات", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            title = { Text("ترقية خطة الوحش البرمجي 👑", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("خيار الترقية نشط بالفعل بحسابك المطور VIP.")
                    Text("• خطة الوحش البرمجي الفائقة غير محدودة النطاق.")
                    Text("• سرعة المعالجة الفائقة بالذكاء الأصيل.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showUpgradeDialog = false }) {
                    Text("تم والحمد لله", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
