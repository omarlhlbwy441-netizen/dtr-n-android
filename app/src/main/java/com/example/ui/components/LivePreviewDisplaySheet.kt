package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusGreen

enum class PreviewTab(val titleAr: String, val icon: ImageVector) {
    WEBSITES("المواقع والويب", Icons.Default.Language),
    GAMES("الألعاب والفيزياء", Icons.Default.SportsEsports),
    UIS("الواجهات الرسومية", Icons.Default.ViewQuilt),
    VIDEOS("الفيديوهات والعروض", Icons.Default.PlayCircleOutline),
    PROJECTS("المشاريع المبنية", Icons.Default.FolderSpecial)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivePreviewDisplaySheet(
    onDismiss: () -> Unit,
    onViewCode: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(PreviewTab.WEBSITES) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = {
            BottomSheetDefaults.DragHandle()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldPrimary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "شاشة العرض والمعاينة الحية 📺",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "المشاريع • الواجهات • المواقع • الألعاب 2D/3D • المرئيات",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PreviewTab.values()) { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        onClick = { selectedTab = tab },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = tab.titleAr,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Content Area Based on Selected Tab
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    PreviewTab.WEBSITES -> WebsitesPreviewView()
                    PreviewTab.GAMES -> GamesPlayablePreviewView()
                    PreviewTab.UIS -> UIComponentsPreviewView(onViewCode = onViewCode)
                    PreviewTab.VIDEOS -> VideosAndMediaPreviewView()
                    PreviewTab.PROJECTS -> ProjectsShowcasePreviewView(onViewCode = onViewCode)
                }
            }
        }
    }
}

// 1. WEBSITES & WEB APPS PREVIEW
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebsitesPreviewView() {
    var webUrl by remember { mutableStateOf("https://dtr-no.onrender.com") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = webUrl,
                onValueChange = { webUrl = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("أدخل رابط الموقع لمعاينته حيّاً...", fontSize = 12.sp) },
                shape = RoundedCornerShape(10.dp)
            )
            Button(
                onClick = { },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("تحديث", fontSize = 12.sp)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = WebViewClient()
                        loadUrl(webUrl)
                    }
                },
                update = { webView ->
                    if (webView.url != webUrl) {
                        webView.loadUrl(webUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// 2. PLAYABLE 2D/3D GAMES PREVIEW
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun GamesPlayablePreviewView() {
    var gameType by remember { mutableStateOf("Space Shooter") }

    val gameHtmlCode = remember(gameType) {
        if (gameType == "Space Shooter") {
            """
            <!DOCTYPE html>
            <html>
            <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
              body { margin:0; background:#0f172a; color:#fff; font-family:sans-serif; text-align:center; overflow:hidden; }
              canvas { background:#020617; display:block; margin:0 auto; width:100%; height:320px; border-radius:12px; }
              .btn { background:#3b82f6; color:#fff; border:none; padding:10px 20px; font-weight:bold; border-radius:8px; margin:8px; font-size:16px; cursor:pointer;}
            </style>
            </head>
            <body>
            <h3 style="margin:8px;">🚀 لعبة Space Battle 2D</h3>
            <canvas id="c"></canvas>
            <div>
              <button class="btn" onclick="moveLeft()">◀ يسار</button>
              <button class="btn" onclick="shoot()">🔥 إطلاق</button>
              <button class="btn" onclick="moveRight()">يمين ▶</button>
            </div>
            <script>
              const canvas = document.getElementById('c');
              const ctx = canvas.getContext('2d');
              canvas.width = window.innerWidth;
              canvas.height = 320;
              let px = canvas.width/2, bullets = [], enemies = [], score = 0;
              function draw() {
                ctx.fillStyle = '#020617'; ctx.fillRect(0,0,canvas.width,canvas.height);
                ctx.fillStyle = '#38bdf8'; ctx.fillRect(px-15, canvas.height-30, 30, 20);
                ctx.fillStyle = '#ef4444';
                bullets.forEach((b,i)=>{ b.y-=5; ctx.fillRect(b.x-2,b.y,5,10); if(b.y<0) bullets.splice(i,1); });
                ctx.fillStyle = '#10b981';
                if(Math.random()<0.03) enemies.push({x:Math.random()*(canvas.width-20),y:0});
                enemies.forEach((e,i)=>{
                  e.y+=2; ctx.fillRect(e.x,e.y,20,20);
                  bullets.forEach((b,bi)=>{
                    if(Math.abs(b.x-e.x)<15 && Math.abs(b.y-e.y)<15) { enemies.splice(i,1); bullets.splice(bi,1); score+=10; }
                  });
                });
                ctx.fillStyle = '#f59e0b'; ctx.font = '16px sans-serif'; ctx.fillText('النقاط: '+score, 10, 25);
                requestAnimationFrame(draw);
              }
              function moveLeft(){ px = Math.max(20, px-25); }
              function moveRight(){ px = Math.min(canvas.width-20, px+25); }
              function shoot(){ bullets.push({x:px, y:canvas.height-30}); }
              draw();
            </script>
            </body>
            </html>
            """.trimIndent()
        } else {
            """
            <!DOCTYPE html>
            <html>
            <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
              body { margin:0; background:#0f172a; color:#fff; font-family:sans-serif; text-align:center; }
              canvas { background:#1e293b; display:block; margin:10px auto; width:100%; height:320px; border-radius:12px; }
            </style>
            </head>
            <body>
            <h3 style="margin:8px;">⚽ محاكاة الفيزياء والارتداد Canvas 2D</h3>
            <canvas id="c"></canvas>
            <p>انقر على الشاشة لإضافة كرات متفاعلة مع الجاذبية!</p>
            <script>
              const canvas = document.getElementById('c');
              const ctx = canvas.getContext('2d');
              canvas.width = window.innerWidth;
              canvas.height = 320;
              let balls = [];
              canvas.addEventListener('click', (e)=>{
                const rect = canvas.getBoundingClientRect();
                balls.push({x: e.clientX - rect.left, y: e.clientY - rect.top, vx: (Math.random()-0.5)*8, vy: 0, color: '#'+Math.floor(Math.random()*16777215).toString(16)});
              });
              function loop(){
                ctx.fillStyle = '#1e293b'; ctx.fillRect(0,0,canvas.width,canvas.height);
                balls.forEach(b=>{
                  b.vy += 0.3; b.x += b.vx; b.y += b.vy;
                  if(b.y > canvas.height-12) { b.y = canvas.height-12; b.vy *= -0.75; }
                  if(b.x < 12 || b.x > canvas.width-12) { b.vx *= -1; }
                  ctx.fillStyle = b.color || '#f43f5e';
                  ctx.beginPath(); ctx.arc(b.x, b.y, 12, 0, Math.PI*2); ctx.fill();
                });
                requestAnimationFrame(loop);
              }
              loop();
            </script>
            </body>
            </html>
            """.trimIndent()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = gameType == "Space Shooter",
                onClick = { gameType = "Space Shooter" },
                label = { Text("🚀 لعبة الفضاء 2D", fontSize = 12.sp) }
            )
            FilterChip(
                selected = gameType == "Physics Sandbox",
                onClick = { gameType = "Physics Sandbox" },
                label = { Text("⚽ محاكي الفيزياء interactive", fontSize = 12.sp) }
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        loadDataWithBaseURL(null, gameHtmlCode, "text/html", "UTF-8", null)
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL(null, gameHtmlCode, "text/html", "UTF-8", null)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// 3. UI COMPONENTS SHOWCASE PREVIEW
@Composable
private fun UIComponentsPreviewView(onViewCode: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("✨ معرض الواجهات الرسومية والمكونات المصممة:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }

        item {
            // Sample Card Component 1
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("بطاقة الإحصائيات الذكية", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Badge(containerColor = StatusGreen) {
                            Text("Jetpack Compose M3", fontSize = 10.sp, color = Color.White)
                        }
                    }
                    Text("واجهة رسومية لإبراز مؤشرات الأداء والأرباح اليومية مع منحنيات استبيانية.", fontSize = 12.sp)
                    Button(
                        onClick = {
                            onViewCode(
                                """
                                @Composable
                                fun SmartStatsCard() {
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                    ) { ... }
                                }
                                """.trimIndent()
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("عرض الكود البرمجي", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            // Sample Card Component 2
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("شريط التحكم بالأنظمة الهجينة", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("واجهة أزرار هجينة متدرجة الألوان تدعم التفاعل اللمسي والانتقال السلس.", fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {}, shape = RoundedCornerShape(8.dp)) { Text("تشغيل ✅") }
                        OutlinedButton(onClick = {}, shape = RoundedCornerShape(8.dp)) { Text("إعدادات ⚙️") }
                    }
                }
            }
        }
    }
}

// 4. VIDEOS AND MEDIA DEMOS PREVIEW
@Composable
private fun VideosAndMediaPreviewView() {
    var isPlaying by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🎥 العروض الحية والتسجيلات المباشرة:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "تشغيل الفيديو",
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = if (isPlaying) "جاري تشغيل العرض التوضيحي..." else "اضغط لتشغيل فيديو معاينة التطبيق",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("تفاصيل العرض المرئي:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("• فيديو معاينة بناء تطبيق أندرويد متكامل باستخدام الوحش البرمجي", fontSize = 11.sp)
                Text("• الدقة: Full HD 1080p • مدة العرض: 02:45 دقيقة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// 5. PROJECTS SHOWCASE PREVIEW
@Composable
private fun ProjectsShowcasePreviewView(onViewCode: (String) -> Unit) {
    val sampleProjects = listOf(
        Triple("تطبيق وحش البرمجة الذكي", "Android Kotlin / Jetpack Compose / Gemini API", "مشروع متكامل"),
        Triple("منصة الاستضافة الخادمة DTR", "Node.js / Express / Render PostgreSQL", "خادم سحابي"),
        Triple("تطبيق إدارة المهام والمستندات", "Google Drive & GitHub API Integration", "تطبيق سحابي")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(sampleProjects) { (title, tech, tag) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StatusGreen.copy(alpha = 0.15f)
                            ) {
                                Text(tag, fontSize = 10.sp, color = StatusGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(tech, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = { onViewCode("// كود المشروع الرئيسي: $title\npackage com.example...") },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("فتح", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
