# 📋 GitHub Project Board Setup

## Quick Setup (3 steps)

### Step 1: Create Project Board
👉 https://github.com/fathan-11/Openclaude-App/projects/new

- Name: **Openclaude-App Development**
- Description: Project board for tracking development
- Template: **Board** (Kanban)

### Step 2: Add Columns
Add these columns to the board:

| Column | Purpose |
|--------|---------|
| 📥 Backlog | Future tasks |
| 📝 To Do | Ready to work |
| 🔄 In Progress | Active tasks |
| 👀 In Review | PR open |
| ✅ Done | Merged |
| 🚀 Released | Deployed |

### Step 3: Run Setup Script
```bash
python3 .github/setup_project.py
```

This creates:
- 14 labels (feature, bug, hotfix, etc.)
- 4 milestones (v1.0.0 → v2.0.0)
- 14 pre-defined issues

## Automation Rules

Configure in Project Settings → Workflows:

| Trigger | Action |
|---------|--------|
| Issue opened | → Backlog |
| PR opened | → In Review |
| PR merged | → Done |
| Release created | → Released |

## Manual Link

After creating the board, link issues to it:
1. Open any issue
2. Click "Projects" in sidebar
3. Select "Openclaude-App Development"
4. Set column status
