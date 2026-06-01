# UI/UX Design Document

## Design System
- **Primary**: #1976D2 (Light) / #90CAF9 (Dark)
- **Background**: #FAFAFA / #121212
- **Error**: #D32F2F / #EF9A9A
- **Spacing**: 4/8/16/24/32 dp scale

## Screen Designs

### User List
- TopAppBar: "Users" with refresh icon
- Shimmer loading (6 placeholder cards)
- LazyColumn with UserCards (48dp avatar + name + email)
- Pull-to-refresh with Material indicator
- Empty: Inbox icon + "No users found" + Try Again
- Error: Warning icon + message + Retry button

### User Detail
- TopAppBar: back arrow + "User Detail"
- 120dp circular avatar
- Full name (headline) + email (caption)
- Info card: Email, User ID, Username rows

## Component Specs
- **UserCard**: RoundedCorner(12dp), 2dp elevation, 16dp padding
- **Shimmer**: InfiniteTransition, 1200ms loop, gradient brush
- **Error**: Warning icon 64dp, error color, Retry button
- **Empty**: Inbox icon 64dp, secondary color, Try Again button

## Interactions
- Tap card → Ripple + navigate to detail
- Pull-to-refresh → Material pull indicator
- Retry → Button spinner → re-fetch
- Back → Slide transition
