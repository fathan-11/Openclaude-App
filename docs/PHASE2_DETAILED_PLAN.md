# 📋 Phase 2 — Detailed Development Plan
## OpenClaude Android v1.1.0 — Code & Files

**Duration:** 4 Weeks
**Goal:** Browse files, view code with syntax highlighting, see diffs, and search code

---

## 📅 Week 6: File System & Data Layer

### Sprint 2.1 — File Models & API (Days 1-3)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 2.1.1 | Create `FileNode` model (file/folder tree) | 🔴 | 1h | ⬜ TODO |
| 2.1.2 | Create `FileContent` model (file content + metadata) | 🔴 | 30m | ⬜ TODO |
| 2.1.3 | Create `DiffLine` model (diff parsing) | 🔴 | 1h | ⬜ TODO |
| 2.1.4 | Create `SearchResult` model | 🔴 | 30m | ⬜ TODO |
| 2.1.5 | Implement `FileApiService` (list/read/write files) | 🔴 | 2h | ⬜ TODO |
| 2.1.6 | Implement `FileRepository` | 🔴 | 2h | ⬜ TODO |
| 2.1.7 | Add file operations to DI module | 🔴 | 30m | ⬜ TODO |

**Deliverable:** File data layer with API integration

### Sprint 2.2 — File Use Cases (Days 4-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 2.2.1 | Implement `BrowseFilesUseCase` | 🔴 | 1h | ⬜ TODO |
| 2.2.2 | Implement `ReadFileUseCase` | 🔴 | 1h | ⬜ TODO |
| 2.2.3 | Implement `SearchCodeUseCase` | 🔴 | 1h | ⬜ TODO |
| 2.2.4 | Implement `GetDiffUseCase` | 🔴 | 1h | ⬜ TODO |
| 2.2.5 | Add file domain models | 🔴 | 30m | ⬜ TODO |

**Deliverable:** File domain layer complete

---

## 📅 Week 7: File Browser UI

### Sprint 2.3 — File Tree Components (Days 1-3)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 2.3.1 | Create `FileTreeItem` component | 🔴 | 2h | ⬜ TODO |
| 2.3.2 | Create `FolderIcon` with expand/collapse | 🔴 | 1h | ⬜ TODO |
| 2.3.3 | Create `FileIcon` by extension | 🔴 | 1h | ⬜ TODO |
| 2.3.4 | Create `BreadcrumbNav` component | 🟡 | 1h | ⬜ TODO |
| 2.3.5 | Create `FileActionMenu` (open, copy, share) | 🟡 | 1h | ⬜ TODO |

**Deliverable:** All file tree components

### Sprint 2.4 — File Browser Screen (Days 4-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 2.4.1 | Implement `FileBrowserScreen` | 🔴 | 3h | ⬜ TODO |
| 2.4.2 | Implement `FileBrowserViewModel` | 🔴 | 2h | ⬜ TODO |
| 2.4.3 | Add lazy tree rendering | 🔴 | 1h | ⬜ TODO |
| 2.4.4 | Add pull-to-refresh | 🟡 | 30m | ⬜ TODO |
| 2.4.5 | Add loading skeleton | 🟡 | 30m | ⬜ TODO |

**Deliverable:** Working file browser

---

## 📅 Week 8: Code Viewer & Syntax Highlighting

### Sprint 2.5 — Syntax Highlighting Engine (Days 1-3)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 2.5.1 | Add TreeSitter or Prism4j dependency | 🔴 | 1h | ⬜ TODO |
| 2.5.2 | Create `SyntaxHighlighter` class | 🔴 | 3h | ⬜ TODO |
| 2.5.3 | Add language detection by extension | 🔴 | 1h | ⬜ TODO |
| 2.5.4 | Add color theme (Dracula/Monokai) | 🟡 | 1h | ⬜ TODO |
| 2.5.5 | Add line numbers component | 🔴 | 1h | ⬜ TODO |

**Deliverable:** Syntax highlighting engine

### Sprint 2.6 — Code Viewer Screen (Days 4-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 2.6.1 | Implement `CodeViewerScreen` | 🔴 | 2h | ⬜ TODO |
| 2.6.2 | Implement `CodeViewerViewModel` | 🔴 | 1h | ⬜ TODO |
| 2.6.3 | Add horizontal scroll | 🔴 | 30m | ⬜ TODO |
| 2.6.4 | Add copy all button | 🔴 | 30m | ⬜ TODO |
| 2.6.5 | Add share button | 🟡 | 30m | ⬜ TODO |
| 2.6.6 | Add font size controls | 🟡 | 1h | ⬜ TODO |
| 2.6.7 | Add search within file | 🟡 | 1h | ⬜ TODO |

**Deliverable:** Working code viewer

---

## 📅 Week 9: Diff Viewer & Search

### Sprint 2.7 — Diff Viewer (Days 1-3)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 2.7.1 | Create `DiffLine` component | 🔴 | 2h | ⬜ TODO |
| 2.7.2 | Create `DiffHunk` component | 🔴 | 1h | ⬜ TODO |
| 2.7.3 | Implement `DiffViewerScreen` | 🔴 | 2h | ⬜ TODO |
| 2.7.4 | Implement `DiffViewerViewModel` | 🔴 | 1h | ⬜ TODO |
| 2.7.5 | Add side-by-side mode | 🟡 | 2h | ⬜ TODO |
| 2.7.6 | Add unified mode | 🔴 | 1h | ⬜ TODO |

**Deliverable:** Working diff viewer

### Sprint 2.8 — Code Search (Days 4-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 2.8.1 | Create `SearchBar` component | 🔴 | 1h | ⬜ TODO |
| 2.8.2 | Create `SearchResultItem` component | 🔴 | 1h | ⬜ TODO |
| 2.8.3 | Implement `SearchScreen` | 🔴 | 2h | ⬜ TODO |
| 2.8.4 | Implement `SearchViewModel` | 🔴 | 1h | ⬜ TODO |
| 2.8.5 | Add regex search support | 🟡 | 1h | ⬜ TODO |
| 2.8.6 | Add search filters (file type, path) | 🟡 | 1h | ⬜ TODO |
| 2.8.7 | Add search history | 🟢 | 30m | ⬜ TODO |

**Deliverable:** Working code search

---

## 🎯 Phase 2 Milestones

| Milestone | Target | Criteria |
|-----------|--------|----------|
| M1: File Data | Week 6 End | File API works, models defined |
| M2: File Browser | Week 7 End | Browse files, expand folders |
| M3: Code Viewer | Week 8 End | View code with syntax highlighting |
| M4: Diff + Search | Week 9 End | View diffs, search code |

---

## 📦 New Dependencies

```kotlin
// Syntax highlighting
implementation("io.github.aspect:aspect-runtime:1.0.0")
// OR
implementation("io.noties:prism4j:2.0.0")
implementation("io.noties:prism4j-bundler:2.0.0")

// Diff parsing
implementation("io.github.java-diff-utils:java-diff-utils:4.12")

// Search
implementation("me.xdrop:fuzzywuzzy:1.4.0")
```

---

## 🧪 Testing Strategy

| Layer | Tool | Coverage |
|-------|------|----------|
| Unit | JUnit 5 + MockK | 80%+ |
| Integration | File API mock | Key flows |
| UI | Compose Testing | File tree, code view |

---

## 📊 Success Metrics

| Metric | Target |
|--------|--------|
| File load time | < 500ms |
| Syntax highlight | < 200ms for 1000 lines |
| Search latency | < 1s |
| Memory usage | < 100MB for large files |
