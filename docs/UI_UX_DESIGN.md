# UI/UX Design Document — OpenDesign System
## KotlinRepoPattern (Openclaude-App)

> **Design System**: OpenDesign v1.0
> **Platform**: Android (API 26+)
> **Framework**: Jetpack Compose + Material 3
> **Accessibility**: WCAG 2.1 AA compliant
> **Last Updated**: 2026-06-01

---

## 1. Screens Overview

| # | Screen | Route | Priority | Description |
|---|--------|-------|----------|-------------|
| 1 | User List | `user_list` | High | Main screen showing all users with search, pull-to-refresh |
| 2 | User Detail | `user_detail/{id}` | Medium | Full user profile with avatar and info card |
| 3 | Splash / Loading | (initial) | Low | Shimmer skeleton during first load |

---

## 2. Screen: User List — Layout & Components

### 2.1 Layout Structure

```
<Scaffold>
  ├─ <TopAppBar variant="centerAligned">
  │    ├─ <Typography variant="h6" color="onPrimary">Users</Typography>
  │    └─ <IconButton icon="refresh" color="onPrimary" />
  │
  └─ <Content>
       ├─ [STATE: Loading] → <ShimmerList count={6} />
       ├─ [STATE: Empty]   → <EmptyState />
       ├─ [STATE: Error]   → <ErrorState />
       └─ [STATE: Success] → <ScrollableContent>
```

### 2.2 Component Breakdown

**`<Scaffold>`** — Root layout
- `topBar`: `<TopAppBar variant="centerAligned">`
- `containerColor`: `surface` token

**`<TopAppBar variant="centerAligned">`**
- Background: `primary` token (#1976D2)
- Title: `<Typography variant="h6">` → "Users", color: `onPrimary`
- No navigation icon (root screen)
- Elevation: 0dp (flat design)

**`<ScrollableContent>` (LazyColumn)**
- `contentPadding`: `spacing.lg` (16dp)
- `verticalArrangement`: `spacing.md` (12dp)
- Items: `<UserCard>` components keyed by `user.id`

**`<UserCard>`**
- Component: `<Card variant="elevated">`
- Elevation: `elevation.sm` (2dp)
- Corner radius: `radius.lg` (12dp)
- Padding: `spacing.lg` (16dp)
- Layout: `<Stack direction="horizontal" align="center" gap="spacing.md">`
  - `<Avatar variant="circular" size="48dp">` — user avatar URL
  - `<Stack direction="vertical" gap="spacing.xs">`
    - `<Typography variant="subtitle1" color="onSurface">` — full name
    - `<Typography variant="body2" color="onSurfaceVariant">` — email
- Interaction: `onClick` → navigate to `user_detail/{id}`
- State: Ripple effect on press

**Footer Item**
- `<Stack align="center" padding="spacing.sm">`
  - `<Button variant="text" label="Refresh" onClick={refresh} />`
  - `<Typography variant="caption" color="onSurfaceVariant">` — "Showing {n} users"

### 2.3 States

**Loading State — `<ShimmerList>`**
- Component: `<Skeleton variant="rectangular" />` × 6
- Each skeleton: `<Card>` shape, 72dp height, 12dp radius
- Animation: `<ShimmerEffect duration="1200ms" />`
- Gradient: `surfaceVariant` → `surface` → `surfaceVariant`

**Empty State — `<EmptyState>`**
- Layout: `<Stack align="center" padding="spacing.xl">`
  - `<Icon name="inbox" size="64dp" color="secondary" />`
  - `<Typography variant="h6" color="onBackground">` — "No users found"
  - `<Spacer height="spacing.lg" />`
  - `<Button variant="contained" label="Try Again" onClick={loadUsers} />`

**Error State — `<ErrorState>`**
- Layout: `<Stack align="center" padding="spacing.xl">`
  - `<Icon name="warning" size="64dp" color="error" />`
  - `<Typography variant="h6" color="onBackground">` — error message
  - `<Spacer height="spacing.lg" />`
  - `<Button variant="contained" label="Retry" onClick={loadUsers} />`

---

## 3. Screen: User Detail — Layout & Components

### 3.1 Layout Structure

```
<Scaffold>
  ├─ <TopAppBar variant="centerAligned">
  │    ├─ <IconButton icon="arrow_back" color="onPrimary" onClick={navigateBack} />
  │    └─ <Typography variant="h6" color="onPrimary">User Detail</Typography>
  │
  └─ <Content>
       ├─ [STATE: Loading] → <ShimmerProfile />
       ├─ [STATE: Error]   → <ErrorState />
       └─ [STATE: Success] → <ProfileContent>
```

### 3.2 Component Breakdown — Success State

**`<ProfileContent>`**
- Layout: `<Stack align="center" padding="spacing.xl">`
  - `<Avatar variant="circular" size="120dp" src={user.avatarUrl} />`
  - `<Spacer height="spacing.lg" />`
  - `<Typography variant="h5" color="onBackground">` — full name
  - `<Typography variant="body2" color="onSurfaceVariant">` — email
  - `<Spacer height="spacing.xxl" />`
  - `<InfoCard>`

**`<InfoCard>`**
- Component: `<Card variant="elevated">`
- Elevation: `elevation.sm` (2dp)
- Corner radius: `radius.lg` (12dp)
- Padding: `spacing.lg` (16dp)
- Layout: `<Stack direction="vertical">`
  - `<InfoRow icon="email" label="Email" value={user.email} />`
  - `<Divider variant="fullWidth" spacing="spacing.lg" />`
  - `<InfoRow icon="person" label="User ID" value={user.id} />`
  - `<Divider variant="fullWidth" spacing="spacing.lg" />`
  - `<InfoRow icon="person" label="Username" value="{firstName}.{lastName}" />`

**`<InfoRow>`**
- Layout: `<Stack direction="horizontal" align="center" gap="spacing.md">`
  - `<Icon name={icon} size="24dp" color="primary" />`
  - `<Stack direction="vertical" gap="spacing.xs">`
    - `<Typography variant="caption" color="onSurfaceVariant">` — label
    - `<Typography variant="body1" color="onSurface">` — value

### 3.3 States

**Loading State — `<ShimmerProfile>`**
- `<Stack align="center" padding="spacing.xl">`
  - `<Skeleton variant="circular" size="120dp" />`
  - `<Skeleton variant="text" width="60%" />`
  - `<Skeleton variant="text" width="40%" />`
  - `<Skeleton variant="rectangular" height="200dp" radius="12dp" />`

**Error State** — same as User List Error State with `onRetry={loadUser}`

---

## 4. Interaction Flows

### 4.1 Navigation

```
App Launch
  └─→ NavHost(startRoute="user_list")
        ├─ "user_list" → UserListScreen
        │    └─ onClick(UserCard) → navigate("user_detail/{userId}")
        │         └─ push animation: slideInRight / slideOutLeft
        │
        └─ "user_detail/{id}" → UserDetailScreen
             └─ onClick(BackButton) → navigateBack()
                  └─ pop animation: slideInLeft / slideOutRight
```

### 4.2 Data Refresh Flow

```
User pulls down on UserList
  └─→ Trigger refresh()
       ├─ Set state: Success(isRefreshing=true)
       ├─ Show: <CircularProgressIndicator size="24dp" /> in top bar
       ├─ Fetch from API via getUsersUseCase()
       ├─ On success: update list, set isRefreshing=false
       └─ On error: show <Snackbar variant="error"> with retry action
```

### 4.3 Error Recovery Flow

```
Error state displayed
  └─→ User taps "Retry" button
       ├─ Set state: Loading
       ├─ Show: <ShimmerList /> or <ShimmerProfile />
       ├─ Re-fetch data
       ├─ On success: transition to Success state
       └─ On error: stay in Error state with updated message
```

### 4.4 Offline / Cache Flow

```
App launches without network
  └─→ Repository checks Room cache
       ├─ Cache exists → show cached data (Success state)
       │    └─ <Snackbar variant="info"> "Showing cached data"
       └─ No cache → Error state "No internet connection"
```

---

## 5. Token Usage

### 5.1 Color Tokens

| Token | Light Value | Dark Value | Usage |
|-------|-------------|------------|-------|
| `primary` | #1976D2 | #90CAF9 | TopAppBar, icons, buttons |
| `onPrimary` | #FFFFFF | #000000 | Text on primary surfaces |
| `background` | #FAFAFA | #121212 | Screen background |
| `onBackground` | #212121 | #E0E0E0 | Primary text |
| `surface` | #FFFFFF | #1E1E1E | Card backgrounds |
| `onSurface` | #212121 | #E0E0E0 | Text on cards |
| `surfaceVariant` | #F5F5F5 | #2C2C2C | Shimmer backgrounds |
| `onSurfaceVariant` | #757575 | #AAAAAA | Secondary text, labels |
| `secondary` | #455A64 | #90A4AE | Empty state icons |
| `error` | #D32F2F | #EF9A9A | Error icons, error text |
| `onError` | #FFFFFF | #000000 | Text on error surfaces |

### 5.2 Typography Tokens

| Token | Font | Size | Weight | Line Height | Usage |
|-------|------|------|--------|-------------|-------|
| `h5` | System | 24sp | Bold | 32sp | User name (detail) |
| `h6` | System | 20sp | Bold | 28sp | TopAppBar titles |
| `subtitle1` | System | 16sp | Medium | 24sp | Card primary text |
| `body1` | System | 16sp | Regular | 24sp | Info row values |
| `body2` | System | 14sp | Regular | 20sp | Card secondary text |
| `caption` | System | 12sp | Regular | 16sp | Labels, metadata |

### 5.3 Spacing Tokens

| Token | Value | Usage |
|-------|-------|-------|
| `spacing.xs` | 4dp | Micro gaps |
| `spacing.sm` | 8dp | Small padding |
| `spacing.md` | 12dp | Card gaps, list spacing |
| `spacing.lg` | 16dp | Content padding, card padding |
| `spacing.xl` | 24dp | Section padding, detail padding |
| `spacing.xxl` | 32dp | Large sections |

### 5.4 Elevation Tokens

| Token | Value | Usage |
|-------|-------|-------|
| `elevation.none` | 0dp | TopAppBar |
| `elevation.sm` | 2dp | Cards |

### 5.5 Radius Tokens

| Token | Value | Usage |
|-------|-------|-------|
| `radius.sm` | 8dp | Buttons |
| `radius.lg` | 12dp | Cards |
| `radius.full` | 50% | Avatars |

### 5.6 Icon Tokens

| Icon Name | Size | Color | Usage |
|-----------|------|-------|-------|
| `arrow_back` | 24dp | `onPrimary` | Back navigation |
| `refresh` | 24dp | `onPrimary` | Refresh action |
| `inbox` | 64dp | `secondary` | Empty state |
| `warning` | 64dp | `error` | Error state |
| `email` | 24dp | `primary` | Email info row |
| `person` | 24dp | `primary` | User info rows |

---

## 6. Accessibility (WCAG 2.1 AA)

- **Color contrast**: All text meets 4.5:1 ratio against background
- **Touch targets**: Minimum 48dp × 48dp for all interactive elements
- **Content descriptions**: All `<Avatar>` and `<Icon>` have `contentDescription`
- **Screen reader**: `<Card>` uses `semantics { role = Role.Button }` for tap actions
- **Focus order**: Logical top-to-bottom, left-to-right traversal
- **Dynamic type**: All `<Typography>` scales with system font size (sp units)
- **Error announcements**: Error states use `LiveRegion` for screen reader announcement

---

## 7. Responsive Behavior

| Breakpoint | Behavior |
|------------|----------|
| < 360dp (small phone) | Single column, 16dp padding |
| 360–600dp (standard phone) | Single column, 16dp padding |
| > 600dp (tablet) | Two-column master-detail layout |

---

## 8. Performance Constraints (from TRD)

- **Shimmer load time**: < 200ms to show skeleton
- **List scroll**: 60fps, no jank on LazyColumn
- **Cache hit**: < 50ms from Room query
- **Network timeout**: 30s max, then error state
- **Image loading**: Coil with memory + disk cache, placeholder shown immediately

---

## 9. Component Inventory

| Component | Variant | Screen | Count |
|-----------|---------|--------|-------|
| `<Scaffold>` | default | Both | 2 |
| `<TopAppBar>` | centerAligned | Both | 2 |
| `<Typography>` | h5, h6, subtitle1, body1, body2, caption | Both | 8 |
| `<Card>` | elevated | Both | 2 |
| `<Avatar>` | circular | Both | 2 |
| `<Button>` | contained, text | Both | 3 |
| `<IconButton>` | default | Both | 3 |
| `<Icon>` | inbox, warning, email, person, arrow_back, refresh | Both | 6 |
| `<Stack>` | vertical, horizontal | Both | 10+ |
| `<Divider>` | fullWidth | Detail | 2 |
| `<Skeleton>` | rectangular, circular, text | Both | 4 |
| `<Snackbar>` | error, info | List | 1 |
| `<Spacer>` | height | Both | 4 |

---

*End of UI/UX Design Document*
