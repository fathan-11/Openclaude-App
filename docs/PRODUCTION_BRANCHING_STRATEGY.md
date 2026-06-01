# 🏭 Production Branching Strategy

## Branch Hierarchy

```
main (production)          ← Protected, releases only
├── staging                ← Pre-production testing
│   └── develop           ← Integration branch
│       ├── feature/*     ← New features
│       ├── bugfix/*      ← Bug fixes
│       └── refactor/*    ← Code refactoring
└── hotfix/*              ← Critical production fixes
```

## Branch Rules

### 🟢 `main` (Production)
- **Purpose:** Production-ready code only
- **Protection:** Maximum (see below)
- **Merge from:** `staging` (via PR) or `hotfix/*` (via PR)
- **Direct commits:** ❌ FORBIDDEN
- **Deployment:** Auto-deploy to Play Store (internal track)
- **Tag:** Auto-tagged with version number

### 🟡 `staging` (Pre-Production)
- **Purpose:** Final testing before production
- **Protection:** High (see below)
- **Merge from:** `develop` (via PR)
- **Direct commits:** ❌ FORBIDDEN
- **Deployment:** Auto-build signed APK + GitHub Release
- **Access:** QA team + developers

### 🔵 `develop` (Integration)
- **Purpose:** Feature integration and daily builds
- **Protection:** Medium (see below)
- **Merge from:** `feature/*`, `bugfix/*`, `refactor/*` (via PR)
- **Direct commits:** ❌ FORBIDDEN
- **Deployment:** Auto-build debug APK
- **Access:** All developers

### 🟣 `feature/*` (Development)
- **Purpose:** Individual feature development
- **Protection:** None (freestyle)
- **Merge from:** Developer creates from `develop`
- **Merge to:** `develop` (via PR)
- **Naming:** `feature/descriptive-name`
- **Lifecycle:** Delete after merge

### 🔴 `hotfix/*` (Emergency)
- **Purpose:** Critical production fixes
- **Protection:** Expedited review (1 approval)
- **Merge from:** Developer creates from `main`
- **Merge to:** `main` AND `staging` AND `develop`
- **Naming:** `hotfix/issue-number-description`
- **Lifecycle:** Delete after merge to all branches

## Merge Flow

```
feature/user-search ──PR──→ develop ──PR──→ staging ──PR──→ main
                                                            ↑
hotfix/crash-fix ────PR─────────────────────────────────────┘
```

## Environment Matrix

| Branch    | Environment | URL                                    | APK Type  | Auto-Deploy |
|-----------|-------------|----------------------------------------|-----------|-------------|
| `develop` | Development | Internal artifact                      | Debug     | ✅ Yes      |
| `staging` | Staging     | staging.openclaude-app.fathan-11.dev   | Signed    | ✅ Yes      |
| `main`    | Production  | Play Store (internal track)            | Signed    | ✅ Yes      |
| `hotfix`  | Emergency   | Manual distribution                    | Signed    | ✅ Yes      |

## CI/CD Pipeline Summary

```
PR → develop:  Lint + Test + Build Debug → Artifact
Push → develop:  Build Debug → Upload Artifact
Push → staging:  Lint + Test + Build Release → Sign → GitHub Release
Push → main:     Lint + Test + Build Release → Sign → APK + AAB → Play Store
Push → hotfix:   Test + Build Release → Sign → Artifact
```

## Required Status Checks

### `main` branch:
- ✅ Lint & Static Analysis
- ✅ Unit Tests
- ✅ Build Debug APK
- ✅ Security Scan
- ✅ 2 Code Owner approvals
- ✅ Conversation resolution
- ✅ Branch up-to-date

### `staging` branch:
- ✅ Lint & Static Analysis
- ✅ Unit Tests
- ✅ Build Debug APK
- ✅ 1 approval

### `develop` branch:
- ✅ Lint & Static Analysis
- ✅ Unit Tests
- ✅ 1 approval

## Version Strategy

- **Major:** Breaking changes (v2.0.0)
- **Minor:** New features (v1.1.0)
- **Patch:** Bug fixes (v1.0.1)
- **Tag format:** `v{major}.{minor}.{patch}`

## Hotfix Protocol

1. Create `hotfix/*` branch from `main`
2. Fix the issue
3. Push → triggers emergency CI
4. Get 1 approval (expedited)
5. Merge to `main` → production deploy
6. Cherry-pick/merge to `staging` and `develop`
7. Delete hotfix branch
