# Application Flow

## Data Flow
```
User Action → UI → ViewModel → UseCase → Repository → DataSources → back up
```

## Screen Flow
```
App Launch → User List Screen
  ├── Loading → Shimmer skeleton UI
  ├── Success → LazyColumn with UserCards
  │     ├── Tap user → User Detail Screen
  │     └── Pull down → Refresh from API
  ├── Empty → "No users found" + Try Again
  └── Error → Error message + Retry
```

## State Transitions
```
Initial: Idle → Loading → Success | Error | Empty
Refresh: Success → Success(refreshing) → Success(fresh)
Retry:   Error → Loading → Success | Error
Offline: Loading → Success(cached) | Error(no cache)
```

## Navigation
```
NavHost(start = "user_list")
  ├── "user_list" → UserListScreen
  │     └── navigate("user_detail/{userId}")
  └── "user_detail/{id}" → UserDetailScreen
        └── navigateBack()
```
