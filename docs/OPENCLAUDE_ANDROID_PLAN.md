# 📱 OpenClaude Android App — Development Plan

## What is OpenClaude?

**OpenClaude** (https://github.com/Gitlawb/openclaude) is an open-source coding-agent CLI with 28k+ stars.

- **45 tools** (bash, file ops, web search, MCP, etc.)
- **100+ slash commands** (/plan, /review, /commit, /agents, etc.)
- **200+ AI models** (OpenAI, Gemini, Ollama, Codex, DeepSeek, etc.)
- **Features**: Voice mode, skills, memory, tasks, GitHub integration, sandbox
- **License**: MIT

---

## 🎯 App Vision

**OpenClaude Android** — A native Android app that brings the OpenClaude coding agent to mobile.

### Core Value Proposition
- **Chat interface** to interact with OpenClaude agents
- **Multi-provider support** (OpenAI, Gemini, Ollama, etc.)
- **Code viewer** with syntax highlighting
- **File browser** for project navigation
- **Terminal emulator** for running commands
- **GitHub integration** for PRs, issues, reviews

---

## 🏗️ Architecture

```
┌─────────────────────────────────────┐
│           UI Layer (Compose)        │
│  Chat │ Files │ Terminal │ Settings  │
├─────────────────────────────────────┤
│         ViewModel Layer             │
│  ChatVM │ FileVM │ TerminalVM │ ... │
├─────────────────────────────────────┤
│          Domain Layer               │
│  Use Cases │ Models │ Interfaces    │
├─────────────────────────────────────┤
│           Data Layer                │
│  API Client │ Local DB │ Prefs      │
├─────────────────────────────────────┤
│        Service Layer                │
│  AI Provider │ WebSocket │ Sync     │
└─────────────────────────────────────┘
```

---

## 📱 Screens & Features

### Phase 1: Core Chat (MVP) — v1.0.0

| Screen | Description | Priority |
|--------|-------------|----------|
| **Splash** | App launch + auth check | 🔴 High |
| **Onboarding** | Provider setup wizard | 🔴 High |
| **Chat List** | All conversations | 🔴 High |
| **Chat View** | Message thread with AI | 🔴 High |
| **Settings** | API keys, model selection | 🔴 High |

#### Chat View Features
- Message bubbles (user / assistant)
- Streaming text response
- Code block syntax highlighting
- Markdown rendering
- Copy code button
- Regenerate response
- Stop generation
- Image attachment (multimodal)

### Phase 2: Code & Files — v1.1.0

| Screen | Description | Priority |
|--------|-------------|----------|
| **File Browser** | Project directory tree | 🔴 High |
| **Code Viewer** | Syntax-highlighted code | 🔴 High |
| **Diff Viewer** | Before/after changes | 🟡 Medium |
| **Search** | Full-text code search | 🟡 Medium |

#### File Features
- Browse project files
- Open/view code with syntax highlighting
- Create/edit/delete files
- Git status indicators
- File search (grep)

### Phase 3: Terminal & Tools — v1.2.0

| Screen | Description | Priority |
|--------|-------------|----------|
| **Terminal** | Command execution | 🟡 Medium |
| **Tool Output** | Tool execution results | 🟡 Medium |
| **MCP Servers** | Manage MCP connections | 🟡 Medium |

#### Terminal Features
- Run bash/shell commands
- Command history
- Output streaming
- ANSI color support
- Kill running processes

### Phase 4: GitHub Integration — v2.0.0

| Screen | Description | Priority |
|--------|-------------|----------|
| **Repos** | Repository browser | 🟡 Medium |
| **PRs** | Pull request list + diff | 🟡 Medium |
| **Issues** | Issue list + create | 🟡 Medium |
| **Reviews** | Code review interface | 🟢 Low |

#### GitHub Features
- OAuth login
- Browse repos
- View/create PRs
- View/create issues
- Code review with inline comments
- Commit history

### Phase 5: Advanced — v2.1.0

| Feature | Description | Priority |
|---------|-------------|----------|
| **Voice Mode** | Speech-to-text input | 🟡 Medium |
| **Skills** | Custom skill management | 🟡 Medium |
| **Memory** | Agent memory viewer | 🟡 Medium |
| **Tasks** | Background task manager | 🟢 Low |
| **Multi-agent** | Agent orchestration | 🟢 Low |
| **Offline** | Local model (Ollama) | 🟢 Low |

---

## 🎨 UI Design System

### Design Tokens
| Token | Light | Dark |
|-------|-------|------|
| primary | #6366F1 (Indigo) | #818CF8 |
| background | #FAFAFA | #0F0F23 |
| surface | #FFFFFF | #1A1A2E |
| userBubble | #6366F1 | #6366F1 |
| aiBubble | #F3F4F6 | #2D2D44 |
| code | #1E1E1E | #282A36 |

### Typography
| Element | Font | Size |
|---------|------|------|
| Message | Inter | 16sp |
| Code | JetBrains Mono | 14sp |
| Header | Inter Bold | 20sp |
| Caption | Inter | 12sp |

### Components (Material 3)
- `ChatBubble` — Message container
- `CodeBlock` — Syntax-highlighted code
- `ToolCard` — Tool execution result
- `FileTreeItem` — Directory tree node
- `ProviderChip` — AI provider selector
- `CommandPalette` — Slash command picker
- `StreamingIndicator` — Typing animation

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material 3 |
| Language | Kotlin 1.9.22 |
| DI | Hilt |
| Navigation | Compose Navigation |
| Networking | Retrofit + OkHttp (SSE) |
| Database | Room (local cache) |
| Preferences | DataStore |
| Images | Coil |
| Syntax | TreeSitter / Prism4j |
| Markdown | Markwon |
| Terminal | Termux libterminal |
| WebSocket | OkHttp WebSocket |
| GitHub | GitHub REST API |
| Auth | OAuth 2.0 |
| Analytics | Firebase |
| CI/CD | GitHub Actions |

---

## 📦 Module Structure

```
app/
├── feature/
│   ├── chat/          # Chat screens + ViewModels
│   ├── files/         # File browser + code viewer
│   ├── terminal/      # Terminal emulator
│   ├── github/        # GitHub integration
│   ├── settings/      # Settings screens
│   └── onboarding/    # Setup wizard
├── core/
│   ├── ui/            # Design system, components
│   ├── data/          # Repository, data sources
│   ├── domain/        # Use cases, models
│   ├── network/       # API clients, SSE
│   ├── database/      # Room, DAOs
│   └── common/        # Utils, extensions
└── app/               # Application, DI, Nav
```

---

## 📅 Timeline

```
Phase 1: Core Chat (MVP)      ████████████░░░░░░░░  Week 1-5
Phase 2: Code & Files          ░░░░░░░░░░░░████████  Week 6-9
Phase 3: Terminal & Tools      ░░░░░░░░░░░░░░░░████  Week 10-12
Phase 4: GitHub Integration    ░░░░░░░░░░░░░░░░░░██  Week 13-15
Phase 5: Advanced              ░░░░░░░░░░░░░░░░░░░█  Week 16-18
```

---

## 🎯 Milestones

| Milestone | Week | Features |
|-----------|------|----------|
| v1.0.0 MVP | 5 | Chat, providers, streaming, settings |
| v1.1.0 | 9 | File browser, code viewer, search |
| v1.2.0 | 12 | Terminal, tool output, MCP |
| v2.0.0 | 15 | GitHub (repos, PRs, issues) |
| v2.1.0 | 18 | Voice, skills, memory, tasks |

---

## 🔑 API Integration

### OpenAI-Compatible API
```kotlin
// Streaming chat completion
POST /v1/chat/completions
{
  "model": "gpt-4",
  "messages": [...],
  "stream": true
}
```

### Supported Providers
| Provider | Auth | Endpoint |
|----------|------|----------|
| OpenAI | API Key | api.openai.com |
| Gemini | API Key | generativelanguage.googleapis.com |
| Ollama | None | localhost:11434 |
| DeepSeek | API Key | api.deepseek.com |
| OpenRouter | API Key | openrouter.ai/api |
| Codex | OAuth | api.codex.so |

---

## 🧪 Testing Strategy

| Layer | Tool | Coverage |
|-------|------|----------|
| Unit | JUnit + MockK | 80%+ |
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
| Play Store rating | 4.5+ |

---

## 🚀 Next Steps

1. **Create Android project** with Clean Architecture
2. **Set up CI/CD** pipeline
3. **Implement chat UI** with streaming
4. **Add provider configuration** (OpenAI, Gemini, Ollama)
5. **Build and test** MVP
6. **Iterate** through phases

---

## 📚 References

- [OpenClaude GitHub](https://github.com/Gitlawb/openclaude)
- [OpenClaude Docs](https://github.com/Gitlawb/openclaude/tree/main/docs)
- [OpenAI API Docs](https://platform.openai.com/docs)
- [Material 3 Design](https://m3.material.io/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
