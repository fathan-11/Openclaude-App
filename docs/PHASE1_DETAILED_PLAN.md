# 📋 Phase 1 — Detailed Development Plan
## OpenClaude Android MVP (v1.0.0)

**Duration:** 5 Weeks
**Goal:** Ship a fully functional AI chat app with multi-provider support

---

## 📅 Week 1: Foundation & Core Architecture

### Sprint 1.1 — Project Setup (Days 1-2)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 1.1.1 | Create Android project with Clean Architecture | 🔴 | 2h | ✅ Done |
| 1.1.2 | Configure Gradle with all dependencies | 🔴 | 1h | ✅ Done |
| 1.1.3 | Set up Hilt DI modules | 🔴 | 2h | ✅ Done |
| 1.1.4 | Create Room database + DAOs | 🔴 | 2h | ✅ Done |
| 1.1.5 | Create DataStore for preferences | 🔴 | 1h | ✅ Done |
| 1.1.6 | Set up CI/CD pipeline | 🟡 | 1h | ✅ Done |
| 1.1.7 | Create .gitignore + README | 🟢 | 30m | ✅ Done |

**Deliverable:** Working project skeleton with DI, DB, and preferences

### Sprint 1.2 — Data Layer (Days 3-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 1.2.1 | Implement `ChatMessage` model | 🔴 | 30m | ✅ Done |
| 1.2.2 | Implement `Conversation` model | 🔴 | 30m | ✅ Done |
| 1.2.3 | Implement `Provider` enum with configs | 🔴 | 1h | ✅ Done |
| 1.2.4 | Implement `ApiService` (OpenAI-compatible) | 🔴 | 2h | ✅ Done |
| 1.2.5 | Implement `StreamingClient` (SSE) | 🔴 | 3h | ✅ Done |
| 1.2.6 | Implement `ChatRepository` | 🔴 | 2h | ✅ Done |
| 1.2.7 | Implement `SettingsRepository` | 🔴 | 1h | ✅ Done |
| 1.2.8 | Implement DTOs (Request/Response) | 🔴 | 1h | ✅ Done |

**Deliverable:** Working data layer with API integration

---

## 📅 Week 2: Domain Layer & Use Cases

### Sprint 2.1 — Use Cases (Days 1-3)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 2.1.1 | Implement `SendMessageUseCase` | 🔴 | 2h | ✅ Done |
| 2.1.2 | Implement `GetConversationsUseCase` | 🔴 | 1h | ✅ Done |
| 2.1.3 | Implement `GetModelsUseCase` | 🔴 | 1h | ✅ Done |
| 2.1.4 | Add domain models | 🔴 | 1h | ✅ Done |
| 2.1.5 | Add error handling wrapper | 🔴 | 1h | ✅ Done |

**Deliverable:** Clean domain layer with all use cases

### Sprint 2.2 — ViewModels (Days 4-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 2.2.1 | Implement `ChatViewModel` | 🔴 | 3h | ✅ Done |
| 2.2.2 | Implement `ConversationListViewModel` | 🔴 | 1h | ✅ Done |
| 2.2.3 | Implement `SettingsViewModel` | 🔴 | 1h | ✅ Done |
| 2.2.4 | Add StateFlow + UI state management | 🔴 | 2h | ✅ Done |
| 2.2.5 | Add loading/error/success states | 🔴 | 1h | ✅ Done |

**Deliverable:** All ViewModels with proper state management

---

## 📅 Week 3: UI Components

### Sprint 3.1 — Theme & Design System (Days 1-2)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 3.1.1 | Create color tokens (indigo theme) | 🔴 | 30m | ✅ Done |
| 3.1.2 | Create typography (Inter + JetBrains Mono) | 🔴 | 30m | ✅ Done |
| 3.1.3 | Create Material 3 theme | 🔴 | 1h | ✅ Done |
| 3.1.4 | Add dark/light mode support | 🟡 | 1h | ✅ Done |

**Deliverable:** Complete design system

### Sprint 3.2 — Core Components (Days 3-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 3.2.1 | Implement `ChatBubble` component | 🔴 | 2h | ✅ Done |
| 3.2.2 | Implement `CodeBlock` with copy | 🔴 | 2h | ✅ Done |
| 3.2.3 | Implement `StreamingText` animation | 🔴 | 2h | ✅ Done |
| 3.2.4 | Implement `MarkdownText` renderer | 🔴 | 2h | ✅ Done |
| 3.2.5 | Implement `MessageInput` field | 🔴 | 1h | ✅ Done |
| 3.2.6 | Implement `ProviderChip` selector | 🔴 | 1h | ✅ Done |

**Deliverable:** All UI components built and tested

---

## 📅 Week 4: Screens & Navigation

### Sprint 4.1 — Navigation (Day 1)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 4.1.1 | Create `NavGraph` with routes | 🔴 | 1h | ✅ Done |
| 4.1.2 | Add screen transitions | 🟡 | 30m | ✅ Done |
| 4.1.3 | Add bottom navigation | 🔴 | 1h | ✅ Done |

**Deliverable:** Working navigation between all screens

### Sprint 4.2 — Chat Screen (Days 2-3)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 4.2.1 | Implement chat message list | 🔴 | 2h | ✅ Done |
| 4.2.2 | Implement message input | 🔴 | 1h | ✅ Done |
| 4.2.3 | Add streaming response display | 🔴 | 2h | ✅ Done |
| 4.2.4 | Add code block rendering | 🔴 | 1h | ✅ Done |
| 4.2.5 | Add copy code functionality | 🔴 | 30m | ✅ Done |
| 4.2.6 | Add regenerate response | 🟡 | 1h | ✅ Done |

**Deliverable:** Fully functional chat screen

### Sprint 4.3 — Other Screens (Days 4-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 4.3.1 | Implement ConversationListScreen | 🔴 | 2h | ✅ Done |
| 4.3.2 | Implement SettingsScreen | 🔴 | 2h | ✅ Done |
| 4.3.3 | Add provider selection UI | 🔴 | 1h | ✅ Done |
| 4.3.4 | Add API key input (masked) | 🔴 | 1h | ✅ Done |
| 4.3.5 | Add model presets | 🟡 | 30m | ✅ Done |

**Deliverable:** All screens implemented

---

## 📅 Week 5: Testing & Polish

### Sprint 5.1 — Testing (Days 1-3)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 5.1.1 | Unit tests for ViewModels | 🔴 | 4h | ⬜ TODO |
| 5.1.2 | Unit tests for Repository | 🔴 | 3h | ⬜ TODO |
| 5.1.3 | Unit tests for Use Cases | 🟡 | 2h | ⬜ TODO |
| 5.1.4 | UI tests for Chat screen | 🟡 | 3h | ⬜ TODO |
| 5.1.5 | Integration test for API | 🟡 | 2h | ⬜ TODO |

**Deliverable:** 80%+ code coverage

### Sprint 5.2 — Polish & Bug Fixes (Days 4-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 5.2.1 | Fix edge cases in streaming | 🔴 | 2h | ⬜ TODO |
| 5.2.2 | Add error handling for API failures | 🔴 | 2h | ⬜ TODO |
| 5.2.3 | Add network connectivity check | 🟡 | 1h | ⬜ TODO |
| 5.2.4 | Optimize image loading | 🟢 | 1h | ⬜ TODO |
| 5.2.5 | Add ProGuard rules | 🟡 | 1h | ⬜ TODO |
| 5.2.6 | Add app icon | 🟡 | 1h | ⬜ TODO |
| 5.2.7 | Performance testing | 🟡 | 2h | ⬜ TODO |

**Deliverable:** Production-ready v1.0.0

---

## 🎯 Phase 1 Milestones

| Milestone | Target | Criteria |
|-----------|--------|----------|
| M1: Foundation | Week 1 End | Project builds, DI works, DB works |
| M2: Data Layer | Week 2 End | API calls work, streaming works |
| M3: UI Complete | Week 3 End | All components built |
| M4: Screens Done | Week 4 End | All screens navigable |
| M5: MVP Ready | Week 5 End | Tests pass, APK works |

---

## 📦 Dependencies

### Core
```kotlin
// Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.06.00"))
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-tooling-preview")

// Hilt
implementation("com.google.dagger:hilt-android:2.50")
kapt("com.google.dagger:hilt-compiler:2.50")
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

// Retrofit + OkHttp
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")

// Moshi
implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

// DataStore
implementation("androidx.datastore:datastore-preferences:1.1.1")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// Coil (images)
implementation("io.coil-kt:coil-compose:2.6.0")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")
```

---

## 🔧 Technical Decisions

| Decision | Choice | Reason |
|----------|--------|--------|
| Architecture | Clean Architecture + MVVM | Testability, separation of concerns |
| DI | Hilt | Official Android DI, Compose support |
| Database | Room | Official, type-safe, migration support |
| Preferences | DataStore | Modern replacement for SharedPreferences |
| Networking | Retrofit + OkHttp | Industry standard, SSE support |
| JSON | Moshi | Kotlin-first, codegen support |
| Images | Coil | Kotlin-first, Compose native |
| Streaming | OkHttp SSE | Built-in SSE support |

---

## 🧪 Testing Strategy

| Layer | Tool | Coverage |
|-------|------|----------|
| Unit | JUnit 5 + MockK | 80%+ |
| Integration | Robolectric | 70%+ |
| UI | Compose Testing | Key flows |
| E2E | Espresso | Critical paths |

---

## 📊 Success Metrics

| Metric | Target |
|--------|--------|
| App size | < 15 MB |
| Cold start | < 2s |
| Message latency | < 500ms (first token) |
| Crash rate | < 0.1% |
| ANR rate | < 0.05% |

---

## 🚀 Next Steps (Phase 2)

After Phase 1 MVP:
1. File browser with syntax highlighting
2. Code viewer with diff support
3. Full-text search
4. Terminal emulator
5. GitHub integration
