# 🤖 OpenClaude Android

A native Android app for the [OpenClaude](https://github.com/Gitlawb/openclaude) coding agent — chat with AI, write code, run commands, all from your phone.

[![Android CI/CD](https://github.com/fathan-11/Openclaude-App/actions/workflows/android-ci.yml/badge.svg)](https://github.com/fathan-11/Openclaude-App/actions/workflows/android-ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![API](https://img.shields.io/badge/API-31%2B-brightgreen.svg)](https://android-arsenal.com/api?level=31)

## ✨ Features

- 💬 **AI Chat Interface** — Natural language coding assistance with streaming responses
- 📁 **File Browser** — Browse, create, edit, delete, and search code files
- 💻 **Terminal Emulator** — Built-in terminal with tab support and session persistence
- 🔍 **Code Viewer** — Syntax highlighting for 15+ languages with find-in-file search
- 🔧 **Tool Execution** — Execute tools directly from chat messages
- 🔌 **MCP Integration** — Auto-discovery of MCP tools and servers
- 🎨 **ANSI Colors** — Full 256-color terminal support
- 🔀 **Git Status** — Visual indicators for modified, added, and deleted files
- 📤 **Share Files** — Share code files via Android share intents
- ⚡ **Performance** — Baseline profiles and large file optimization
- 🌙 **Dark Mode** — Beautiful dark theme
- 📱 **Material 3** — Modern Android UI components
- 🔄 **Multi-Provider** — OpenAI, Gemini, Ollama, DeepSeek, OpenRouter
- 🌐 **Network Monitoring** — Connectivity state awareness

## 📱 Screenshots

Coming soon...

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture + MVVM |
| DI | Hilt |
| Networking | Retrofit + OkHttp (SSE) |
| Database | Room |
| Preferences | DataStore |
| Language | Kotlin 1.9.22 |
| Min SDK | 31 (Android 12) |
| Target SDK | 34 (Android 14) |

## 🚀 Setup

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### Installation

1. Clone the repository
   ```bash
   git clone https://github.com/fathan-11/Openclaude-App.git
   cd Openclaude-App
   ```

2. Open in Android Studio

3. Sync Gradle

4. Run on device/emulator

### Configuration

1. Open the app
2. Go to Settings
3. Select your AI provider
4. Enter your API key
5. Choose a model
6. Start chatting!

## 🔑 API Keys

| Provider | Get Key |
|----------|---------|
| OpenAI | [platform.openai.com/api-keys](https://platform.openai.com/api-keys) |
| Google | [aistudio.google.com](https://aistudio.google.com/) |
| OpenRouter | [openrouter.ai/keys](https://openrouter.ai/keys) |
| DeepSeek | [platform.deepseek.com](https://platform.deepseek.com/) |
| Ollama | Local — no key needed |

## 📁 Project Structure

```
app/src/main/java/com/openclaude/android/
├── data/
│   ├── model/          # Data models
│   ├── remote/         # API services
│   ├── repository/     # Repository implementations
│   └── network/        # Network monitoring
├── domain/
│   ├── model/          # Domain models
│   └── usecase/        # Business logic
├── ui/
│   ├── screens/        # Compose screens
│   │   ├── chat/       # AI chat interface
│   │   ├── files/      # File browser
│   │   ├── terminal/   # Terminal emulator
│   │   ├── codeviewer/ # Code viewer
│   │   └── settings/   # App settings
│   ├── components/     # Reusable UI components
│   └── theme/          # Material 3 theme
└── di/                 # Hilt dependency injection
```

## 🏗️ Architecture

```
UI Layer (Compose)
    ↓
ViewModel (StateFlow)
    ↓
UseCase
    ↓
Repository
    ├── Remote (Retrofit + OkHttp SSE)
    └── Local (Room + DataStore)
```

## 🤝 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [OpenClaude](https://github.com/Gitlawb/openclaude) — The AI coding agent
- [Jetpack Compose](https://developer.android.com/jetpack/compose) — Modern Android UI
- [Hilt](https://dagger.dev/hilt/) — Dependency injection
