# Contributing to KotlinRepoPattern

Thank you for your interest in contributing! This document outlines the process for contributing to this project.

## Branching Strategy

- **main** — Production-ready code. Protected. No direct commits.
- **develop** — Integration branch for features (if applicable).
- **feature/*** — New features (e.g., `feature/user-search`)
- **bugfix/*** — Bug fixes (e.g., `bugfix/login-crash`)
- **refactor/*** — Code refactoring (e.g., `refactor/repository-layer`)
- **hotfix/*** — Critical production fixes

## Pull Request Rules

### Creation
- PR title must be descriptive: `FEAT: Add search`, `FIX: Crash on logout`
- PR description must include: What, Why, How
- Link related issues: `Closes #123`
- Draft PRs for work-in-progress

### Reviews
- **2 approvals required** before merging
- Authors cannot approve their own PRs
- Code owners must review affected areas
- All review comments must be addressed

### Automated Checks
- All CI/CD checks must pass (lint, test, build)
- No merge if checks are failing

### Merge Strategy
- **Squash and Merge** — Preferred for feature branches
- **Rebase and Merge** — For small, clean bug fixes
- **Merge Commit** — Only for complex features with curated history

### Post-Merge
- Delete the feature branch
- Deployment triggers automatically from `main`

## Code Standards

- Kotlin coding conventions
- Clean Architecture + MVVM
- Meaningful variable/function names
- Comments for complex logic
- Unit tests for business logic

## Hotfixes

Critical fixes follow an expedited process:
- 1 approval minimum
- Must pass critical CI checks
- Clearly communicated to the team

## Questions?

Open an issue or reach out to @fathan-11.
