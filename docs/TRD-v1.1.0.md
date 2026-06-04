# Technical Requirements Document (TRD)
## OpenClaude Android v1.1.0 — WebView & Web Development Tools

**Document Version:** 1.0  
**Date:** June 4, 2026  
**Author:** Hermes Agent (Technical Lead)  
**Status:** DRAFT  
**Reviewers:** fathan-11, Development Team

---

## Table of Contents

1. [Technical Overview](#1-technical-overview)
2. [Architecture Design](#2-architecture-design)
3. [Technology Stack](#3-technology-stack)
4. [Component Specifications](#4-component-specifications)
5. [API Design](#5-api-design)
6. [Database Design](#6-database-design)
7. [Security Architecture](#7-security-architecture)
8. [Performance Requirements](#8-performance-requirements)
9. [Testing Strategy](#9-testing-strategy)
10. [Deployment](#10-deployment)
11. [Monitoring & Observability](#11-monitoring--observability)
12. [Appendices](#12-appendices)

---

## 1. Technical Overview

### 1.1 System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    OpenClaude Android                       │
├─────────────────────────────────────────────────────────────┤
│  UI Layer (Jetpack Compose)                                 │
│  ├── Chat Screen                                            │
│  ├── File Browser                                           │
│  ├── Terminal                                               │
│  └── WebView Screen (NEW)                                   │
│      ├── Browser Component                                  │
│      ├── DevTools Panel                                     │
│      └── Device Emulator                                    │
├─────────────────────────────────────────────────────────────┤
│  ViewModel Layer (MVVM)                                     │
│  ├── WebViewViewModel                                       │
│  ├── DevToolsViewModel                                      │
│  └── SecurityViewModel                                      │
├─────────────────────────────────────────────────────────────┤
│  Domain Layer (Use Cases)                                   │
│  ├── LoadUrlUseCase                                         │
│  ├── CaptureConsoleUseCase                                  │
│  ├── MonitorNetworkUseCase                                  │
│  ├── InspectElementUseCase                                  │
│  └── SecurityScanUseCase                                    │
├─────────────────────────────────────────────────────────────┤
│  Data Layer                                                 │
│  ├── WebViewRepository                                      │
│  ├── DevToolsRepository                                     │
│  ├── SecurityRepository                                     │
│  └── Local Storage (Room + DataStore)                       │
├─────────────────────────────────────────────────────────────┤
│  Platform Layer                                             │
│  ├── Android WebView                                        │
│  ├── Chrome DevTools Protocol                               │
│  └── Security Manager                                       │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Technical Goals

| Goal | Implementation |
|------|----------------|
| Modular architecture | Clean Architecture + Hilt DI |
| Performance | Baseline profiles, lazy loading |
| Security | Process isolation, sandboxing |
| Testability | Unit tests, integration tests |
| Maintainability | Kotlin, Compose, coroutines |

---

## 2. Architecture Design

### 2.1 Clean Architecture Layers

```
Presentation (UI)
    ↓ observes StateFlow
ViewModel
    ↓ calls UseCase
Domain (Business Logic)
    ↓ calls Repository
Data (Repository)
    ├── Remote (API, Network)
    └── Local (Room, DataStore)
```

### 2.2 Component Architecture

```
WebViewFeature
├── BrowserModule
│   ├── WebViewWrapper (Kotlin)
│   ├── UrlValidator
│   ├── HistoryManager
│   └── BookmarkManager
├── DevToolsModule
│   ├── ConsoleCapture
│   ├── NetworkInterceptor
│   ├── DOMInspector
│   └── PerformanceMonitor
├── SecurityModule
│   ├── SandboxManager
│   ├── PermissionController
│   └── SecurityScanner
└── EmulationModule
    ├── DeviceProfiles
    ├── ViewportManager
    └── TouchSimulator
```

### 2.3 State Management

```kotlin
// WebView Screen State
data class WebViewUiState(
    val url: String = "",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val title: String = "",
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isDesktopMode: Boolean = false,
    val devToolsVisible: Boolean = false,
    val activeDevToolsTab: DevToolsTab = DevToolsTab.CONSOLE,
    val securityMode: SecurityMode = SecurityMode.SANDBOX
)

// DevTools State
data class DevToolsState(
    val consoleLogs: List<ConsoleLog> = emptyList(),
    val networkRequests: List<NetworkRequest> = emptyList(),
    val selectedElement: DomElement? = null,
    val performanceMetrics: PerformanceMetrics? = null
)

// Security State
data class SecurityState(
    val sandboxEnabled: Boolean = true,
    val permissions: Map<String, PermissionStatus> = emptyMap(),
    val vulnerabilities: List<Vulnerability> = emptyList(),
    val securityScore: Int = 100
)
```

---

## 3. Technology Stack

### 3.1 Core Technologies

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| Language | Kotlin | 1.9.22 | Primary language |
| UI Framework | Jetpack Compose | 1.6.0 | Declarative UI |
| Design System | Material 3 | 1.2.0 | UI components |
| Architecture | Clean Architecture | — | Code organization |
| DI | Hilt | 2.50 | Dependency injection |
| Navigation | Compose Navigation | 2.7.0 | Screen navigation |
| State | StateFlow + Compose | — | Reactive state |

### 3.2 WebView Technologies

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| WebView | Android WebView | Chrome 100+ | Web rendering |
| JS Bridge | JavaScriptInterface | — | Native-JS communication |
| Protocol | Chrome DevTools Protocol | — | DevTools integration |
| Network | OkHttp Interceptor | 4.12 | Request monitoring |
| Security | WebSettings | — | Security configuration |

### 3.3 Data Layer

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| Database | Room | 2.6.0 | Local storage |
| Preferences | DataStore | 1.0.0 | Settings storage |
| Cache | Coil | 2.5.0 | Image caching |
| Serialization | Kotlinx Serialization | 1.6.0 | JSON handling |

### 3.4 Testing

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| Unit Testing | JUnit | 5.10.0 | Unit tests |
| UI Testing | Compose Testing | 1.6.0 | UI tests |
| Mocking | Mockk | 1.13.0 | Mocking framework |
| Integration | Espresso | 3.5.1 | Integration tests |

---

## 4. Component Specifications

### 4.1 WebView Wrapper

**File:** `app/src/main/java/com/openclaude/android/ui/components/WebViewWrapper.kt`

```kotlin
@Composable
fun WebViewWrapper(
    url: String,
    isLoading: Boolean,
    onUrlChange: (String) -> Unit,
    onPageStarted: () -> Unit,
    onPageFinished: () -> Unit,
    onConsoleMessage: (ConsoleLog) -> Unit,
    onNetworkRequest: (NetworkRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    // Implementation
}
```

**Requirements:**
- Render HTML/CSS/JavaScript
- Support HTTP/HTTPS protocols
- Handle JavaScript execution
- Capture console messages
- Intercept network requests
- Support file:// protocol for local files

**Performance Targets:**
- Initial render: <500ms
- JavaScript execution: <100ms
- Memory usage: <100MB

### 4.2 Console Capture

**File:** `app/src/main/java/com/openclaude/android/data/devtools/ConsoleCapture.kt`

```kotlin
class ConsoleCapture {
    private val _logs = MutableSharedFlow<ConsoleLog>()
    val logs: SharedFlow<ConsoleLog> = _logs.asSharedFlow()
    
    fun attachToWebView(webView: WebView) {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                _logs.tryEmit(
                    ConsoleLog(
                        level = consoleMessage.messageLevel(),
                        message = consoleMessage.message(),
                        sourceId = consoleMessage.sourceId(),
                        lineNumber = consoleMessage.lineNumber(),
                        timestamp = System.currentTimeMillis()
                    )
                )
                return true
            }
        }
    }
}
```

**Data Model:**
```kotlin
data class ConsoleLog(
    val level: ConsoleMessage.MessageLevel,
    val message: String,
    val sourceId: String,
    val lineNumber: Int,
    val timestamp: Long
)
```

### 4.3 Network Interceptor

**File:** `app/src/main/java/com/openclaude/android/data/devtools/NetworkInterceptor.kt`

```kotlin
class NetworkInterceptor {
    private val _requests = MutableSharedFlow<NetworkRequest>()
    val requests: SharedFlow<NetworkRequest> = _requests.asSharedFlow()
    
    fun createInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val startTime = System.nanoTime()
            
            val response = chain.proceed(request)
            
            val duration = (System.nanoTime() - startTime) / 1_000_000
            
            _requests.tryEmit(
                NetworkRequest(
                    url = request.url.toString(),
                    method = request.method,
                    headers = request.headers.toMap(),
                    status = response.code,
                    duration = duration,
                    size = response.body?.contentLength() ?: 0,
                    timestamp = System.currentTimeMillis()
                )
            )
            
            response
        }
    }
}
```

**Data Model:**
```kotlin
data class NetworkRequest(
    val url: String,
    val method: String,
    val headers: Map<String, String>,
    val status: Int,
    val duration: Long,
    val size: Long,
    val timestamp: Long
)
```

### 4.4 Sandbox Manager

**File:** `app/src/main/java/com/openclaude/android/data/security/SandboxManager.kt`

```kotlin
class SandboxManager @Inject constructor(
    private val permissionController: PermissionController
) {
    fun configureWebViewSettings(settings: WebSettings) {
        settings.apply {
            // Security settings
            javaScriptEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            
            // Performance settings
            cacheMode = WebSettings.LOAD_DEFAULT
            domStorageEnabled = true
            databaseEnabled = true
            
            // Privacy settings
            setGeolocationEnabled(false)
            saveFormData = false
            savePassword = false
        }
    }
    
    fun enforceSandboxPolicy(webView: WebView) {
        // Block navigation to untrusted URLs
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return !isUrlAllowed(request.url.toString())
            }
        }
    }
    
    private fun isUrlAllowed(url: String): Boolean {
        // URL validation logic
        return url.startsWith("https://") || 
               url.startsWith("file://")
    }
}
```

---

## 5. API Design

### 5.1 Internal APIs

#### WebViewRepository Interface

```kotlin
interface WebViewRepository {
    suspend fun loadUrl(url: String): Result<WebViewState>
    suspend fun loadLocalFile(path: String): Result<WebViewState>
    suspend fun getPageSource(): Result<String>
    suspend fun takeScreenshot(): Result<Bitmap>
    suspend fun getPerformanceMetrics(): Result<PerformanceMetrics>
}
```

#### DevToolsRepository Interface

```kotlin
interface DevToolsRepository {
    // Console
    fun getConsoleLogs(): Flow<List<ConsoleLog>>
    suspend fun clearConsoleLogs()
    suspend fun executeJavaScript(script: String): Result<String>
    
    // Network
    fun getNetworkRequests(): Flow<List<NetworkRequest>>
    suspend fun clearNetworkRequests()
    suspend fun getRequestDetails(id: String): Result<RequestDetails>
    
    // Elements
    suspend fun inspectElement(selector: String): Result<DomElement>
    suspend fun getDomTree(): Result<List<DomElement>>
    suspend fun modifyElement(selector: String, changes: Map<String, String>): Result<Unit>
}
```

#### SecurityRepository Interface

```kotlin
interface SecurityRepository {
    suspend fun scanUrl(url: String): Result<SecurityReport>
    suspend fun getPermissions(): Result<Map<String, PermissionStatus>>
    suspend fun requestPermission(permission: String): Result<Boolean>
    suspend fun blockUrl(url: String): Result<Unit>
    suspend fun getSecurityScore(): Result<Int>
}
```

### 5.2 Chrome DevTools Protocol Integration

```kotlin
class ChromeDevToolsBridge {
    suspend fun connect(webView: WebView) {
        // Enable DevTools protocol
        webView.evaluateJavascript(
            """
            (function() {
                // Inject DevTools agent
                window.__OPENCLAUDE_DEVTOOLS__ = {
                    inspect: function(selector) { /* ... */ },
                    getPerformance: function() { /* ... */ },
                    getConsole: function() { /* ... */ }
                };
            })();
            """.trimIndent()
        ) { }
    }
    
    suspend fun evaluate(expression: String): Result<DevToolsResponse> {
        // Evaluate JavaScript expression
        return suspendCancellableCoroutine { continuation ->
            webView.evaluateJavascript(expression) { result ->
                continuation.resume(
                    DevToolsResponse(
                        result = result,
                        success = true
                    )
                )
            }
        }
    }
}
```

---

## 6. Database Design

### 6.1 Room Entities

```kotlin
@Entity(tableName = "web_sessions")
data class WebSessionEntity(
    @PrimaryKey val id: String,
    val url: String,
    val title: String,
    val timestamp: Long,
    val duration: Long,
    val screenshotPath: String?
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val url: String,
    val title: String,
    val favicon: String?,
    val createdAt: Long
)

@Entity(tableName = "console_logs")
data class ConsoleLogEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val level: String,
    val message: String,
    val sourceId: String,
    val lineNumber: Int,
    val timestamp: Long
)
```

### 6.2 DataStore Preferences

```kotlin
// WebView Settings
val WEBVIEW_SETTINGS = stringPreferencesKey("webview_settings")
val SECURITY_MODE = stringPreferencesKey("security_mode")
val DESKTOP_MODE = booleanPreferencesKey("desktop_mode")
val JAVASCRIPT_ENABLED = booleanPreferencesKey("javascript_enabled")

// DevTools Settings
val CONSOLE_VISIBLE = booleanPreferencesKey("console_visible")
val NETWORK_MONITORING = booleanPreferencesKey("network_monitoring")
val AUTO_REFRESH = booleanPreferencesKey("auto_refresh")
```

---

## 7. Security Architecture

### 7.1 Security Layers

```
Layer 1: Network Security
├── HTTPS enforcement
├── Certificate validation
├── Mixed content blocking
└── HSTS compliance

Layer 2: Content Security
├── CSP header validation
├── XSS prevention
├── Input sanitization
└── URL validation

Layer 3: Process Security
├── WebView process isolation
├── JavaScript sandboxing
├── File system restrictions
└── Memory limits

Layer 4: Data Security
├── Encrypted storage
├── Secure preferences
├── Token management
└── Session isolation
```

### 7.2 Permission Model

```kotlin
enum class WebPermission(val displayName: String) {
    JAVASCRIPT("JavaScript Execution"),
    FILE_ACCESS("File System Access"),
    CAMERA("Camera Access"),
    MICROPHONE("Microphone Access"),
    LOCATION("Location Access"),
    NOTIFICATIONS("Notifications"),
    COOKIES("Cookie Access"),
    LOCAL_STORAGE("Local Storage"),
    CLIPBOARD("Clipboard Access")
}

data class PermissionStatus(
    val permission: WebPermission,
    val granted: Boolean,
    val alwaysAsk: Boolean = true,
    val lastRequested: Long? = null
)
```

### 7.3 Security Scanner Rules

```kotlin
enum class SecurityRule(
    val id: String,
    val severity: Severity,
    val description: String
) {
    MIXED_CONTENT("SEC001", Severity.HIGH, "Page loads HTTP resources on HTTPS"),
    INSECURE_FORM("SEC002", Severity.HIGH, "Form submits to HTTP endpoint"),
    MISSING_CSP("SEC003", Severity.MEDIUM, "No Content-Security-Policy header"),
    XSS_RISK("SEC004", Severity.CRITICAL, "Potential XSS vulnerability detected"),
    OPEN_REDIRECT("SEC005", Severity.MEDIUM, "Open redirect vulnerability"),
    INSECURE_COOKIE("SEC006", Severity.LOW, "Cookie without Secure flag")
}
```

---

## 8. Performance Requirements

### 8.1 Memory Management

```kotlin
class MemoryManager {
    private val maxMemory = 200 * 1024 * 1024L // 200MB
    
    fun monitorMemoryUsage(webView: WebView): Flow<MemoryStatus> = flow {
        while (true) {
            val runtime = Runtime.getRuntime()
            val usedMemory = runtime.totalMemory() - runtime.freeMemory()
            
            when {
                usedMemory > maxMemory * 0.9 -> {
                    emit(MemoryStatus.CRITICAL)
                    webView.freeMemory()
                }
                usedMemory > maxMemory * 0.7 -> {
                    emit(MemoryStatus.WARNING)
                }
                else -> {
                    emit(MemoryStatus.NORMAL)
                }
            }
            
            delay(5000) // Check every 5 seconds
        }
    }
}
```

### 8.2 Lazy Loading

```kotlin
// Lazy load devtools panel
@Composable
fun DevToolsPanel(
    activeTab: DevToolsTab,
    devToolsState: DevToolsState
) {
    when (activeTab) {
        DevToolsTab.CONSOLE -> {
            // Only render when tab is active
            ConsoleView(logs = devToolsState.consoleLogs)
        }
        DevToolsTab.NETWORK -> {
            NetworkView(requests = devToolsState.networkRequests)
        }
        DevToolsTab.ELEMENTS -> {
            // Lazy load DOM tree
            LazyColumn {
                items(devToolsState.domTree) { element ->
                    DomElementRow(element)
                }
            }
        }
        DevToolsTab.PERFORMANCE -> {
            PerformanceView(metrics = devToolsState.performanceMetrics)
        }
    }
}
```

### 8.3 Caching Strategy

```kotlin
class CacheManager {
    private val diskCache = DiskLruCache(
        directory = context.cacheDir,
        maxSize = 50 * 1024 * 1024L, // 50MB
        namespace = "webview-cache"
    )
    
    suspend fun getCachedResponse(url: String): Response? {
        return withContext(Dispatchers.IO) {
            val key = url.toMD5()
            diskCache.get(key)?.let { entry ->
                entry.getSource().use { source ->
                    Json.decodeFromString<Response>(source.buffer.readUtf8())
                }
            }
        }
    }
}
```

---

## 9. Testing Strategy

### 9.1 Test Pyramid

```
                /\
               /  \
              / E2E\     10% - UI Tests
             /______\
            /        \
           /Integration\  30% - Integration Tests
          /____________\
         /              \
        /     Unit       \  60% - Unit Tests
       /__________________\
```

### 9.2 Test Categories

#### Unit Tests (60%)

```kotlin
// ConsoleCaptureTest.kt
class ConsoleCaptureTest {
    @Test
    fun `console message is captured`() = runTest {
        val capture = ConsoleCapture()
        val logs = mutableListOf<ConsoleLog>()
        
        capture.logs.collect { logs.add(it) }
        
        // Simulate console message
        // Assert log is captured
        assertEquals(1, logs.size)
    }
}

// SandboxManagerTest.kt
class SandboxManagerTest {
    @Test
    fun `https url is allowed`() {
        val manager = SandboxManager()
        assertTrue(manager.isUrlAllowed("https://example.com"))
    }
    
    @Test
    fun `http url is blocked`() {
        val manager = SandboxManager()
        assertFalse(manager.isUrlAllowed("http://example.com"))
    }
}
```

#### Integration Tests (30%)

```kotlin
// WebViewIntegrationTest.kt
@RunWith(AndroidJUnit4::class)
class WebViewIntegrationTest {
    @Test
    fun `webview loads url`() {
        onView(withId(R.id.urlInput))
            .perform(typeText("https://example.com"), pressImeActionButton())
        
        onView(withId(R.id.webView))
            .check(matches(isDisplayed()))
    }
}
```

#### E2E Tests (10%)

```kotlin
// WebViewE2ETest.kt
@RunWith(AndroidJUnit4::class)
class WebViewE2ETest {
    @Test
    fun `full workflow - load, inspect, debug`() {
        // Navigate to WebView
        // Enter URL
        // Wait for load
        // Open devtools
        // Check console
        // Inspect elements
        // Verify network requests
    }
}
```

### 9.3 Performance Testing

```kotlin
// PerformanceBenchmark.kt
@RunWith(AndroidJUnit4::class)
class PerformanceBenchmark {
    @Test
    fun `webview cold start benchmark`() {
        val benchmarkRule = BenchmarkRule()
        
        benchmarkRule.measureRepeated {
            // Launch WebView
            // Measure time to first paint
        }
    }
    
    @Test
    fun `console render benchmark`() {
        // Generate 1000 console logs
        // Measure render time
        // Assert < 100ms
    }
}
```

---

## 10. Deployment

### 10.1 Build Configuration

```kotlin
// app/build.gradle.kts
android {
    defaultConfig {
        minSdk = 31
        targetSdk = 34
        versionCode = 2
        versionName = "1.1.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### 10.2 CI/CD Pipeline

```yaml
name: Android CI/CD v1.1.0

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Run Unit Tests
        run: ./gradlew testDebugUnitTest
      
      - name: Run Lint
        run: ./gradlew lintDebug
      
      - name: Build Debug APK
        run: ./gradlew assembleDebug
      
      - name: Build Release APK
        run: ./gradlew assembleRelease
      
      - name: Upload APKs
        uses: actions/upload-artifact@v4
        with:
          name: apks
          path: |
            app/build/outputs/apk/debug/*.apk
            app/build/outputs/apk/release/*.apk
```

### 10.3 Release Checklist

- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] Lint checks pass
- [ ] No critical/high security vulnerabilities
- [ ] Performance benchmarks meet targets
- [ ] ProGuard/R8 rules configured
- [ ] Version numbers updated
- [ ] Changelog updated
- [ ] Release notes drafted
- [ ] Signed APK generated
- [ ] Play Store listing updated
- [ ] Beta testing complete
- [ ] Production release approved

---

## 11. Monitoring & Observability

### 11.1 Metrics to Track

| Metric | Tool | Alert Threshold |
|--------|------|-----------------|
| Crash Rate | Crashlytics | >0.1% |
| ANR Rate | Crashlytics | >0.05% |
| WebView Load Time | Custom | >3s |
| Memory Usage | Custom | >200MB |
| Console Errors | Custom | >100/min |
| Network Failures | Custom | >5% |

### 11.2 Logging Strategy

```kotlin
// Use Timber for logging
Timber.plant(if (BuildConfig.DEBUG) DebugTree() else CrashReportingTree())

// Log levels
Timber.d("WebView: Loading URL: %s", url)
Timber.i("DevTools: Console message captured")
Timber.w("Security: Blocked untrusted URL: %s", url)
Timber.e("Performance: Memory usage exceeded threshold")
```

### 11.3 Analytics Events

```kotlin
// Track user actions
analytics.logEvent("webview_url_loaded", mapOf(
    "url_domain" to Uri.parse(url).host,
    "load_time_ms" to loadTime
))

analytics.logEvent("devtools_tab_opened", mapOf(
    "tab_name" to tab.name
))

analytics.logEvent("security_vulnerability_found", mapOf(
    "rule_id" to rule.id,
    "severity" to rule.severity.name
))
```

---

## 12. Appendices

### 12.1 File Structure

```
app/src/main/java/com/openclaude/android/
├── ui/
│   ├── screens/
│   │   └── webview/
│   │       ├── WebViewScreen.kt
│   │       ├── WebViewViewModel.kt
│   │       └── WebViewUiState.kt
│   ├── components/
│   │   ├── WebViewWrapper.kt
│   │   ├── DevToolsPanel.kt
│   │   ├── ConsoleView.kt
│   │   ├── NetworkView.kt
│   │   ├── ElementInspector.kt
│   │   └── DeviceEmulator.kt
│   └── navigation/
│       └── NavGraph.kt
├── domain/
│   ├── usecase/
│   │   ├── LoadUrlUseCase.kt
│   │   ├── CaptureConsoleUseCase.kt
│   │   ├── MonitorNetworkUseCase.kt
│   │   ├── InspectElementUseCase.kt
│   │   └── SecurityScanUseCase.kt
│   └── model/
│       ├── ConsoleLog.kt
│       ├── NetworkRequest.kt
│       ├── DomElement.kt
│       └── SecurityReport.kt
├── data/
│   ├── repository/
│   │   ├── WebViewRepository.kt
│   │   ├── DevToolsRepository.kt
│   │   └── SecurityRepository.kt
│   ├── local/
│   │   ├── WebSessionDao.kt
│   │   ├── BookmarkDao.kt
│   │   └── ConsoleLogDao.kt
│   └── devtools/
│       ├── ConsoleCapture.kt
│       ├── NetworkInterceptor.kt
│       ├── ChromeDevToolsBridge.kt
│       └── SandboxManager.kt
└── di/
    ├── WebViewModule.kt
    ├── DevToolsModule.kt
    └── SecurityModule.kt
```

### 12.2 Dependencies

```kotlin
// app/build.gradle.kts
dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.0")
    
    // WebView
    implementation("androidx.webkit:webkit:1.10.0")
    
    // Data
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // DI
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

### 12.3 ProGuard Rules

```proguard
# WebView
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel
```

---

**Document Status:** DRAFT  
**Next Review:** Jun 7, 2026  
**Approval Required:** fathan-11
