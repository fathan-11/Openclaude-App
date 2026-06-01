# 📋 Phase 3 — Detailed Development Plan
## OpenClaude Android v1.2.0 — Terminal & Tools

**Duration:** 3 Weeks
**Goal:** Run commands, view tool output, manage MCP servers

---

## 📅 Week 10: Terminal Emulator

### Sprint 3.1 — Terminal Data Layer (Days 1-2)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 3.1.1 | Create `TerminalCommand` model | 🔴 | 30m | ⬜ TODO |
| 3.1.2 | Create `TerminalSession` model | 🔴 | 30m | ⬜ TODO |
| 3.1.3 | Create `TerminalOutput` model | 🔴 | 30m | ⬜ TODO |
| 3.1.4 | Implement `TerminalApiService` (run/kill/list) | 🔴 | 2h | ⬜ TODO |
| 3.1.5 | Implement `TerminalRepository` | 🔴 | 2h | ⬜ TODO |
| 3.1.6 | Implement `RunCommandUseCase` | 🔴 | 1h | ⬜ TODO |
| 3.1.7 | Implement `GetCommandHistoryUseCase` | 🟡 | 1h | ⬜ TODO |

**Deliverable:** Terminal data layer

### Sprint 3.2 — Terminal UI (Days 3-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 3.2.1 | Create `TerminalLine` component | 🔴 | 1h | ⬜ TODO |
| 3.2.2 | Create `TerminalInput` component | 🔴 | 1h | ⬜ TODO |
| 3.2.3 | Create `CommandHistoryItem` component | 🟡 | 30m | ⬜ TODO |
| 3.2.4 | Implement `TerminalScreen` | 🔴 | 3h | ⬜ TODO |
| 3.2.5 | Implement `TerminalViewModel` | 🔴 | 2h | ⬜ TODO |
| 3.2.6 | Add ANSI color support | 🔴 | 2h | ⬜ TODO |
| 3.2.7 | Add command history navigation | 🟡 | 1h | ⬜ TODO |
| 3.2.8 | Add kill process button | 🔴 | 30m | ⬜ TODO |

**Deliverable:** Working terminal emulator

---

## 📅 Week 11: Tool Output Viewer

### Sprint 3.3 — Tool Data Layer (Days 1-2)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 3.3.1 | Create `ToolExecution` model | 🔴 | 30m | ⬜ TODO |
| 3.3.2 | Create `ToolResult` model | 🔴 | 30m | ⬜ TODO |
| 3.3.3 | Create `ToolType` enum (bash, file, search, web, mcp) | 🔴 | 30m | ⬜ TODO |
| 3.3.4 | Implement `ToolApiService` | 🔴 | 1h | ⬜ TODO |
| 3.3.5 | Implement `ToolRepository` | 🔴 | 1h | ⬜ TODO |
| 3.3.6 | Implement `ExecuteToolUseCase` | 🔴 | 1h | ⬜ TODO |

**Deliverable:** Tool data layer

### Sprint 3.4 — Tool UI (Days 3-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 3.4.1 | Create `ToolCard` component | 🔴 | 1h | ⬜ TODO |
| 3.4.2 | Create `ToolOutputBlock` component | 🔴 | 1h | ⬜ TODO |
| 3.4.3 | Create `ToolStatusIndicator` component | 🟡 | 30m | ⬜ TODO |
| 3.4.4 | Implement `ToolOutputScreen` | 🔴 | 2h | ⬜ TODO |
| 3.4.5 | Implement `ToolOutputViewModel` | 🔴 | 1h | ⬜ TODO |
| 3.4.6 | Add tool execution from chat | 🔴 | 1h | ⬜ TODO |
| 3.4.7 | Add tool result display in chat | 🔴 | 1h | ⬜ TODO |

**Deliverable:** Tool output viewer

---

## 📅 Week 12: MCP Server Manager

### Sprint 3.5 — MCP Data Layer (Days 1-2)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 3.5.1 | Create `McpServer` model | 🔴 | 30m | ⬜ TODO |
| 3.5.2 | Create `McpTool` model | 🔴 | 30m | ⬜ TODO |
| 3.5.3 | Create `McpResource` model | 🟡 | 30m | ⬜ TODO |
| 3.5.4 | Implement `McpApiService` | 🔴 | 2h | ⬜ TODO |
| 3.5.5 | Implement `McpRepository` | 🔴 | 1h | ⬜ TODO |
| 3.5.6 | Implement `ManageMcpUseCase` | 🔴 | 1h | ⬜ TODO |

**Deliverable:** MCP data layer

### Sprint 3.6 — MCP UI (Days 3-5)

| # | Task | Priority | Est. | Status |
|---|------|----------|------|--------|
| 3.6.1 | Create `McpServerCard` component | 🔴 | 1h | ⬜ TODO |
| 3.6.2 | Create `McpToolItem` component | 🔴 | 30m | ⬜ TODO |
| 3.6.3 | Create `AddServerDialog` component | 🔴 | 1h | ⬜ TODO |
| 3.6.4 | Implement `McpScreen` | 🔴 | 2h | ⬜ TODO |
| 3.6.5 | Implement `McpViewModel` | 🔴 | 1h | ⬜ TODO |
| 3.6.6 | Add server connect/disconnect | 🔴 | 1h | ⬜ TODO |
| 3.6.7 | Add tool execution from MCP | 🟡 | 1h | ⬜ TODO |

**Deliverable:** MCP server manager

---

## 🎯 Phase 3 Milestones

| Milestone | Target | Criteria |
|-----------|--------|----------|
| M1: Terminal | Week 10 End | Run commands, see output |
| M2: Tools | Week 11 End | Execute tools, view results |
| M3: MCP | Week 12 End | Manage MCP servers |

---

## 📦 New Dependencies

```kotlin
// ANSI parsing
implementation("org.fusesource.jansi:jansi:2.4.1")

// WebSocket for streaming terminal
implementation("org.java-websocket:Java-WebSocket:1.5.4")
```

---

## 📊 Success Metrics

| Metric | Target |
|--------|--------|
| Command execution | < 200ms latency |
| ANSI render | Full 256-color support |
| Terminal scroll | 10k+ lines |
| MCP connect | < 3s |
