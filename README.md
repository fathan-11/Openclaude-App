# 🤖 OpenClaude Android

A native Android app for the [OpenClaude](https://github.com/Gitlawb/openclaude) coding agent — chat with AI, write code, run commands, all from your phone.

## Features

- 💬 **Chat Interface** — Natural language coding assistance
- 🔄 **Streaming Responses** — Real-time AI output
- 🎨 **Syntax Highlighting** — Code blocks with copy button
- 🔌 **Multi-Provider** — OpenAI, Gemini, Ollama, DeepSeek, OpenRouter
- 🌙 **Dark Mode** — Beautiful dark theme
- 📱 **Material 3** — Modern Android UI

## Screenshots

Coming soon...

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture + MVVM |
| DI | Hilt |
| Networking | Retrofit + OkHttp (SSE) |
| Database | Room |
| Preferences | DataStore |
| Language | Kotlin 1.9.22 |

## Setup

1. Clone the repo
2. Open in Android Studio
3. Sync Gradle
4. Run on device/emulator

## Configuration

1. Go to Settings
2. Select your AI provider
3. Enter your API key
4. Choose a model
5. Start chatting!

## API Keys

| Provider | Get Key |
|----------|---------|
| OpenAI | https://platform.openai.com/api-keys |
| Gemini | https://aistudio.google.com/apikey |
| DeepSeek | https://platform.deepseek.com/api_keys |
| OpenRouter | https://openrouter.ai/keys |
| Ollama | No key needed (local) |

## Building

```bash
# Debug
./gradlew assembleDebug

# Release
./gradlew assembleRelease
```

## License

MIT License — see [LICENSE](LICENSE)

## Credits

Built on top of [OpenClaude](https://github.com/Gitlawb/openclaude) (28k+ ⭐)
