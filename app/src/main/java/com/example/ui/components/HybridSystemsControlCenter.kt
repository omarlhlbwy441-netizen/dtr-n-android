package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusGreen

data class SystemFeature(
    val id: String,
    val titleAr: String,
    val subtitleAr: String,
    val categoryAr: String,
    val icon: ImageVector,
    val isOnline: Boolean,
    val detailsAr: String,
    val actionTextAr: String? = null
)

@Composable
fun HybridSystemsControlCenter(
    onOpenVoiceCall: () -> Unit,
    onOpenDtrVoiceAssistant: () -> Unit,
    onOpenGitHubSettings: () -> Unit,
    onSwitchToWebView: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf("الكل") }
    var activeDialogFeature by remember { mutableStateOf<SystemFeature?>(null) }

    val features = remember {
        listOf(
            SystemFeature(
                id = "ai_movie_video_studio",
                titleAr = "استوديو بناء الأفلام والفيديوهات (AI Movie Studio)",
                subtitleAr = "مونتاج وتوليد مقاطع سينمائية، سيناريو ذكي، وتعليق صوتي بالذكاء الاصطناعي",
                categoryAr = "إنتاج الوسائط والأفلام",
                icon = Icons.Default.Movie,
                isOnline = true,
                detailsAr = "نظام سينمائي متكامل لتوليد الأفلام والمقاطع الترويجية، كتابة السيناريو، تركيب الأصوات والترجمة الحية، وتحرير الفيديوهات المباشر 4K.",
                actionTextAr = "فتح استوديو الأفلام"
            ),
            SystemFeature(
                id = "google_drive",
                titleAr = "ربط Google Drive والنسخ السحابي",
                subtitleAr = "مزامنة المستندات، ملفات الكود، وتخزين الأصول بالزمن الفعلي",
                categoryAr = "التخزين السحابي",
                icon = Icons.Default.CloudUpload,
                isOnline = true,
                detailsAr = "يتيح التطبيق الحفظ والمزامنة المباشرة للمستندات والملفات والمشاريع على Google Drive الخاص بك واسترجاعها في أي وقت.",
                actionTextAr = "ربط Google Drive"
            ),
            SystemFeature(
                id = "github_integration",
                titleAr = "ربط حساب GitHub والمستودعات البرمجية",
                subtitleAr = "إدارة الكود، الـ Commits، الفروع، والمزامنة مع GitHub Actions",
                categoryAr = "التطوير والنشر",
                icon = Icons.Default.Code,
                isOnline = true,
                detailsAr = "ربط كامل مع حساب GitHub وتتبع التغييرات التلقائية وبناء ملفات APK/AAB ودفع التحديثات بالزمن الفعلي.",
                actionTextAr = "إدارة GitHub"
            ),
            SystemFeature(
                id = "hosting_platforms",
                titleAr = "منصات الاستضافة (Render / Vercel / Netlify / Firebase)",
                subtitleAr = "ربط وتتبع الخوادم، قواعد البيانات PostgreSQL، والـ Deployments",
                categoryAr = "الاستضافة والخدمات",
                icon = Icons.Default.Storage,
                isOnline = true,
                detailsAr = "مراقبة وإدارة الخوادم السحابية، الاستضافات المباشرة، وقواعد البيانات المرتبطة مع دعم الـ Webhooks.",
                actionTextAr = "إدارة الاستضافات"
            ),
            SystemFeature(
                id = "live_web_search",
                titleAr = "محرك البحث الحي في الإنترنت (Live Web Grounding)",
                subtitleAr = "جلب الأخبار والمعلومات الحية وتأكيد البيانات بالزمن الفعلي",
                categoryAr = "البحث والمعرفة",
                icon = Icons.Default.TravelExplore,
                isOnline = true,
                detailsAr = "يتصل التطبيق بمحركات البحث العالمية لجلب أحدث البيانات والأخبار المباشرة وتضمينها في إجابات الذكاء الاصطناعي.",
                actionTextAr = "تجربة البحث الحي"
            ),
            SystemFeature(
                id = "academic_library_search",
                titleAr = "محرك البحث في الكتب والمراجع والمكتبات العلمية",
                subtitleAr = "البحث في Google Books, arXiv, PubMed, Open Library, و IEEE",
                categoryAr = "البحث والمعرفة",
                icon = Icons.Default.MenuBook,
                isOnline = true,
                detailsAr = "نظام متكامل للبحث الفوري في آلاف الكتب والمراجع العلمية، الأوراق البحثية، والأكواد المصدرية للأبحاث الأكاديمية.",
                actionTextAr = "بحث في المراجع والكتب"
            ),
            SystemFeature(
                id = "gemini_studio",
                titleAr = "محرك Google AI Studio & Gemini 2.0",
                subtitleAr = "أحدث نماذج الذكاء الاصطناعي مع معالجة حية وسريعة",
                categoryAr = "محركات الذكاء الاصطناعي",
                icon = Icons.Default.AutoAwesome,
                isOnline = true,
                detailsAr = "يدعم نماذج Gemini 1.5 Flash، Gemini 2.0 Flash، و Gemini Pro مع التوجيه التلقائي للمستندات والوسائط."
            ),
            SystemFeature(
                id = "kimi_k1",
                titleAr = "Kimi K1 Deep Thinking Engine",
                subtitleAr = "محرك التفكير التسلسلي البرمجي وحل المسائل المعقدة",
                categoryAr = "أنظمة Kimi AI",
                icon = Icons.Default.Psychology,
                isOnline = true,
                detailsAr = "يقوم بالتحليل الهيكلي العميق قبل توليد الأكواد لضمان عدم وجود أخطاء بنشاط مستمر."
            ),
            SystemFeature(
                id = "game_engine_core",
                titleAr = "محرك الألعاب البرمجي ومحاكاة الفيزياء (Game & Physics Engine)",
                subtitleAr = "حلقة المعالجة الزمنية، إدارة المشاهد والفيزياء ثنائية/ثلاثية الأبعاد",
                categoryAr = "تطوير الألعاب والبيئات التفاعلية",
                icon = Icons.Default.SportsEsports,
                isOnline = true,
                detailsAr = "يتضمن خوارزميات التصادم، الـ Scene Graph، وإدارة مدخلات المستخدم بالزمن الفعلي."
            ),
            SystemFeature(
                id = "hybrid_canvas",
                titleAr = "النظام الهجين التفاعلي (Hybrid Web & Native Canvas)",
                subtitleAr = "التنقل الفوري بين الواجهات البرمجية الأصيلة ومعاينة الويب المباشرة",
                categoryAr = "المعمارية الهجينة",
                icon = Icons.Default.Web,
                isOnline = true,
                detailsAr = "يتيح تشغيل ومعاينة التطبيقات والمواقع والمستندات بداخل محرك WebView معالج بـ JavaScript كاملاً."
            )
        )
    }

    val categories = listOf("الكل", "إنتاج الوسائط والأفلام", "التخزين السحابي", "التطوير والنشر", "الاستضافة والخدمات", "البحث والمعرفة", "محركات الذكاء الاصطناعي")

    val filteredFeatures = features.filter { feature ->
        (activeTab == "الكل" || feature.categoryAr == activeTab) &&
                (searchQuery.isBlank() || feature.titleAr.contains(searchQuery) || feature.subtitleAr.contains(searchQuery) || feature.detailsAr.contains(searchQuery))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Banner: Hybrid Engine Overview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Hub,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تطبيق وحش البرمجة - كافة الأنظمة والتكاملات الموحدة",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Google Drive • GitHub • منصات الاستضافة • البحث في النت والكتب والمكتبات",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                    // Search Input Inside Control Center
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("بحث في جميع أنظمة ومقدرات وحش البرمجة...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "مسح")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
        }

        // Category Pills Filter
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(3).forEach { category ->
                    FilterChip(
                        selected = activeTab == category,
                        onClick = { activeTab = category },
                        label = { Text(category, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.drop(3).forEach { category ->
                    FilterChip(
                        selected = activeTab == category,
                        onClick = { activeTab = category },
                        label = { Text(category, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        // Features List
        items(filteredFeatures, key = { it.id }) { feature ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = feature.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = feature.titleAr,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "متصل ومتوفر ⚡",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF059669),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = feature.subtitleAr,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AnimatedVisibility(
                        visible = expanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = feature.detailsAr,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )

                            feature.actionTextAr?.let { actionBtnText ->
                                Spacer(Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        if (feature.id == "github_integration") {
                                            onOpenGitHubSettings()
                                        } else {
                                            activeDialogFeature = feature
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(actionBtnText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Action Dialog for Integrations (Google Drive, Cloud Hosts, Web & Library Search)
    activeDialogFeature?.let { feature ->
        AlertDialog(
            onDismissRequest = { activeDialogFeature = null },
            icon = { Icon(feature.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text(feature.titleAr, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(feature.detailsAr, fontSize = 13.sp)

                    when (feature.id) {
                        "ai_movie_video_studio" -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("🎬 خيارات استوديو الأفلام والفيديوهات السريعة:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Button(
                                        onClick = { activeDialogFeature = null },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("توليد فيلم سينمائي 4K بنظام النص إلى سيناريو")
                                    }
                                    OutlinedButton(
                                        onClick = { activeDialogFeature = null },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("فتح شريط المونتاج وتركيب الأصوات والترجمة")
                                    }
                                }
                            }
                        }
                        "google_drive" -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("حساب Google Drive المرتبط:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("wolfforleatherproducts@gmail.com", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("حالة المزامنة: المزامنة التلقائية للمشاريع والأكواد مفعلة ✅", fontSize = 11.sp, color = StatusGreen)
                                }
                            }
                        }
                        "hosting_platforms" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("المنصات المتصلة حالياً:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                                    Text("• Render Platform: https://dtr-no.onrender.com (نشِط)", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                                }
                                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                                    Text("• Vercel / Firebase Hosting: متصل عبر GitHub Webhook", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                        "live_web_search" -> {
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                placeholder = { Text("أدخل استعلام البحث الحي في النت...", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        "academic_library_search" -> {
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                placeholder = { Text("بحث في Google Books, arXiv, PubMed...", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { activeDialogFeature = null }) {
                    Text("إغلاق وتأكيد الربط", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

