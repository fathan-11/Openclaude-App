#!/usr/bin/env python3
"""
GitHub Project Board Setup Script

This script creates labels, milestones, and issues for the project board.
Run: python3 .github/setup_project.py
"""

import subprocess
import json
import os

REPO = "fathan-11/Openclaude-App"

def run(cmd):
    r = subprocess.run(cmd, capture_output=True, text=True, timeout=30)
    return r.returncode, r.stdout.strip(), r.stderr.strip()

def create_label(name, color, description):
    rc, out, err = run([
        "gh", "label", "create", name,
        "--repo", REPO,
        "--color", color,
        "--description", description,
        "--force"
    ])
    status = "✅" if rc == 0 else "❌"
    print(f"  {status} Label: {name}")

def create_milestone(title, description):
    rc, out, err = run([
        "gh", "api", f"repos/{REPO}/milestones",
        "--method", "POST",
        "--field", f"title={title}",
        "--field", f"description={description}"
    ])
    status = "✅" if rc == 0 else "❌"
    print(f"  {status} Milestone: {title}")

def create_issue(title, body, labels, milestone):
    rc, out, err = run([
        "gh", "issue", "create",
        "--repo", REPO,
        "--title", title,
        "--body", body,
        "--label", ",".join(labels),
        "--milestone", milestone
    ])
    status = "✅" if rc == 0 else "❌"
    print(f"  {status} Issue: {title}")

def main():
    # Load config
    script_dir = os.path.dirname(os.path.abspath(__file__))
    with open(os.path.join(script_dir, "project-board.json")) as f:
        config = json.load(f)
    
    print("🏷️  Creating Labels...")
    for label in config["labels"]:
        create_label(label["name"], label["color"], label["description"])
    
    print("\n📅 Creating Milestones...")
    for ms in config["milestones"]:
        create_milestone(ms["title"], ms["description"])
    
    print("\n📝 Creating Issues...")
    for issue in config["issues"]:
        create_issue(issue["title"], issue["body"], issue["labels"], issue["milestone"])
    
    print("\n✅ Project setup complete!")
    print(f"📋 Create project board: https://github.com/{REPO}/projects/new")

if __name__ == "__main__":
    main()
