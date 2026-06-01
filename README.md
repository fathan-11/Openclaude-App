# KotlinRepoPattern

A Kotlin Android application demonstrating **Clean Architecture** with the **Repository Pattern** using Jetpack Compose, Hilt, Room, and Retrofit.

## Architecture

```
UI Layer (Compose) → ViewModel (StateFlow) → UseCase → Repository → DataSources
                                                                    ├── Remote (Retrofit)
                                                                    └── Local (Room)
```

## Tech Stack

| Layer        | Technology                      |
|-------------|---------------------------------|
| UI          | Jetpack Compose, Material 3     |
| Navigation  | Compose Navigation              |
| ViewModel   | Kotlin StateFlow                |
| DI          | Hilt                            |
| Network     | Retrofit + OkHttp + Moshi       |
| Database    | Room                            |
| Async       | Kotlin Coroutines + Flow        |
| Images      | Coil                            |
| Testing     | JUnit, Mockk, Turbine           |

## API

Uses [ReqRes.in](https://reqres.in) as a demo REST API for user data.

## Setup

1. Clone the repo
2. Open in Android Studio (Hedgehog+)
3. Sync Gradle
4. Run on emulator or device (API 26+)

## Features

- ✅ Repository pattern with single source of truth
- ✅ Local-first caching strategy
- ✅ Reactive UI with StateFlow
- ✅ Pull-to-refresh
- ✅ Shimmer loading animation
- ✅ Error handling with retry
- ✅ Empty state handling
- ✅ Dark mode + Dynamic color (Android 12+)
- ✅ Edge-to-edge design

## License

MIT
