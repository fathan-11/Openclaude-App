# Contributing to OpenClaude Android

Thank you for your interest in contributing to OpenClaude Android! This document outlines the process for contributing to this project.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Branching Strategy](#branching-strategy)
- [Pull Request Process](#pull-request-process)
- [Code Standards](#code-standards)
- [Commit Messages](#commit-messages)
- [Reporting Issues](#reporting-issues)

## 📜 Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them get started
- Focus on constructive feedback
- Respect differing viewpoints and experiences

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Git

### Setup

1. Fork the repository on GitHub
2. Clone your fork locally
   ```bash
   git clone https://github.com/your-username/Openclaude-App.git
   cd Openclaude-App
   ```
3. Add upstream remote
   ```bash
   git remote add upstream https://github.com/fathan-11/Openclaude-App.git
   ```
4. Create a feature branch
   ```bash
   git checkout -b feature/your-feature-name
   ```
5. Make your changes
6. Push to your fork
   ```bash
   git push origin feature/your-feature-name
   ```
7. Create a Pull Request

## 🌿 Branching Strategy

- **main** — Production-ready code. Protected. No direct commits.
- **develop** — Integration branch for features (if applicable).
- **feature/*** — New features (e.g., `feature/file-search`)
- **bugfix/*** — Bug fixes (e.g., `bugfix/crash-on-logout`)
- **refactor/*** — Code refactoring (e.g., `refactor/repository-layer`)
- **hotfix/*** — Critical production fixes

## 📝 Pull Request Process

### Creation

- PR title must be descriptive using conventional commits:
  - `feat: Add file search functionality`
  - `fix: Resolve crash on logout`
  - `docs: Update README`
  - `chore: Update dependencies`
- PR description must include:
  - **What** — What does this PR do?
  - **Why** — Why is this change needed?
  - **How** — How does this change work?
- Link related issues: `Closes #123`
- Draft PRs for work-in-progress

### Reviews

- **1 approval required** before merging (for most PRs)
- Authors cannot approve their own PRs
- All review comments must be addressed
- CI/CD checks must pass

### Merge Strategy

- **Squash and Merge** — Preferred for feature branches
- **Rebase and Merge** — For small, clean bug fixes
- **Merge Commit** — Only for complex features with curated history

### Post-Merge

- Delete the feature branch
- Deployment triggers automatically from `main`

## 💻 Code Standards

### Kotlin

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable/function names
- Add comments for complex logic
- Keep functions short and focused

### Architecture

- Follow Clean Architecture principles
- Use MVVM pattern for UI
- Separate concerns properly
- Use Hilt for dependency injection

### Testing

- Write unit tests for business logic
- Aim for 80%+ code coverage
- Test edge cases
- Use descriptive test names

### Compose

- Follow [Compose best practices](https://developer.android.com/jetpack/compose/best-practices)
- Use preview annotations
- Keep composables small and focused
- Use state hoisting

## 📨 Commit Messages

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

### Types

- **feat**: A new feature
- **fix**: A bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, missing semi-colons, etc)
- **refactor**: Code refactoring
- **perf**: Performance improvements
- **test**: Adding or updating tests
- **chore**: Maintenance tasks
- **ci**: CI/CD changes

### Examples

```
feat(files): Add file search functionality
fix(terminal): Resolve crash on empty input
docs: Update README with setup instructions
chore: Update dependencies to latest versions
```

## 🐛 Reporting Issues

### Bug Reports

When filing a bug report, please include:

1. **Description** — Clear and concise description of the bug
2. **Steps to Reproduce** — Steps to reproduce the behavior
3. **Expected Behavior** — What you expected to happen
4. **Actual Behavior** — What actually happened
5. **Screenshots** — If applicable, add screenshots
6. **Environment** — Device model, Android version, app version

### Feature Requests

When requesting a feature, please include:

1. **Description** — Clear and concise description of the feature
2. **Use Case** — Why this feature would be useful
3. **Proposed Solution** — If you have ideas on how to implement it
4. **Alternatives** — Any alternative solutions you've considered

## ❓ Questions?

- Open an issue for questions about the project
- Reach out to @fathan-11 on GitHub
- Check existing issues and discussions first

## 🙏 Thank You!

Thank you for contributing to OpenClaude Android! Your help is greatly appreciated.
