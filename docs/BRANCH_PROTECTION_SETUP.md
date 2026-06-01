# 🔒 Branch Protection Setup

Your token can't configure branch protection via API.
**You need to set this up manually on GitHub:**

## Steps:

1. Go to: https://github.com/fathan-11/Openclaude-App/settings/branches

2. Click **"Add rule"** (or edit existing for `main`)

3. Configure these settings:

### Branch name pattern
```
main
```

### ✅ Protect matching branches

- [x] **Require a pull request before merging**
  - [x] Require approvals: **2**
  - [x] Dismiss stale pull request approvals when new commits are pushed
  - [x] Require review from Code Owners

- [x] **Require status checks to pass before merging**
  - [x] Require branches to be up to date before merging
  - Status checks: `Build & Test`

- [x] **Require conversation resolution before merging**

- [x] **Require linear history**

- [x] **Do not allow bypassing the above settings**
  (includes admins)

- [x] **Restrict who can push to matching branches**
  - Only: @fathan-11

- [x] **Allow force pushes** → ❌ DISABLED

- [x] **Allow deletions** → ❌ DISABLED

4. Click **"Create"** or **"Save changes"**

## Also Enable:

### Auto-delete head branches
Settings → General → Pull Requests → [x] Automatically delete head branches

### Merge button options
Settings → General → Pull Requests:
- [x] Allow squash merging (default commit message: PR title + description)
- [x] Allow rebase merging
- [ ] Allow merge commits → UNCHECK
