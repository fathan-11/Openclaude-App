# 🌐 Environment Configuration

## Development Environment

- **Branch:** `develop`
- **Build:** Debug APK
- **API Base URL:** `https://reqres.in/api/` (demo)
- **Database:** Room (local)
- **Features:** All experimental features enabled
- **Crash Reporting:** Console only

## Staging Environment

- **Branch:** `staging`
- **Build:** Signed Release APK
- **API Base URL:** `https://staging-api.openclaude-app.fathan-11.dev/`
- **Database:** Room (local) + Remote sync
- **Features:** Production features only
- **Crash Reporting:** Firebase Crashlytics (staging project)

## Production Environment

- **Branch:** `main`
- **Build:** Signed Release APK + AAB
- **API Base URL:** `https://api.openclaude-app.fathan-11.dev/`
- **Database:** Room (local) + Remote sync
- **Features:** Stable features only
- **Crash Reporting:** Firebase Crashlytics (production project)
- **Distribution:** Play Store (internal track → beta → production)

## GitHub Environment Secrets

### Required Secrets (Settings → Secrets → Actions)

| Secret                  | Environment  | Description                    |
|------------------------|--------------|--------------------------------|
| `SIGNING_KEY`          | All          | Base64 encoded keystore        |
| `ALIAS`                | All          | Key alias                      |
| `KEY_STORE_PASSWORD`   | All          | Keystore password              |
| `KEY_PASSWORD`          | All          | Key password                   |
| `PLAY_SERVICE_ACCOUNT_JSON` | Production | Play Store service account  |

### Environment Protection Rules

**development:**
- No protection rules
- Auto-deploy on push to `develop`

**staging:**
- Required reviewers: @fathan-11
- Wait timer: 0 minutes
- Auto-deploy on push to `staging`

**production:**
- Required reviewers: @fathan-11
- Wait timer: 5 minutes (cool-down)
- Auto-deploy on push to `main`
