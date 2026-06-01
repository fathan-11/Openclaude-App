# Technical Requirements Document (TRD)

## 1. Architecture: Clean Architecture + MVVM
- **UI Layer**: Jetpack Compose screens
- **ViewModel Layer**: StateFlow-based ViewModels
- **Domain Layer**: Use Cases
- **Data Layer**: Repositories, Data Sources, Models

## 2. Data Models
| Model       | Purpose                    |
|-------------|----------------------------|
| UserDto     | Network response (Moshi)   |
| UserEntity  | Room database entity       |
| User        | Domain model (used in UI)  |

## 3. Repository Strategy
```
getUsers() → Flow<Resource<List<User>>>
  1. Emit Loading
  2. Emit cached data from Room (if exists)
  3. Fetch fresh from Retrofit
  4. Save to Room
  5. Emit fresh data from Room
  6. On error: emit cached or Error
```

## 4. DI (Hilt)
- AppModule: Retrofit, OkHttp, Moshi, ApiService, bindings
- DatabaseModule: Room database, UserDao

## 5. Error Handling
- Network timeout → "Connection timed out"
- HTTP errors → "Server error, try again"
- No connectivity → "No internet, showing cached"
- Empty result → "No users found"

## 6. Tech Stack
Kotlin 1.9.22, Compose 1.6.0, Hilt 2.50, Retrofit 2.9.0, Room 2.6.1, Coroutines 1.7.3
