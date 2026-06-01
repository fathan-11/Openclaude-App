#!/usr/bin/env python3
"""
PR Generator - Creates GitHub PRs from JSON templates

Usage:
    python3 .github/pr-templates/generate_pr.py feature --title "Add search" --branch "feature/user-search"
    python3 .github/pr-templates/generate_pr.py bugfix --title "Fix crash" --branch "bugfix/login-crash"
    python3 .github/pr-templates/generate_pr.py hotfix --title "Fix auth" --branch "hotfix/auth-fix"
"""

import argparse
import json
import subprocess
import os

TEMPLATES = {
    "feature": "feature-pr.json",
    "bugfix": "bugfix-pr.json",
    "hotfix": "hotfix-pr.json",
    "refactor": "refactor-pr.json",
    "release": "release-pr.json",
}

def load_template(template_name):
    script_dir = os.path.dirname(os.path.abspath(__file__))
    path = os.path.join(script_dir, TEMPLATES[template_name])
    with open(path) as f:
        return json.load(f)

def create_pr(template, title, head, base=None, draft=False):
    cmd = [
        "gh", "pr", "create",
        "--title", title or template["title"],
        "--body", template["body"],
        "--head", head,
        "--base", base or template["base"],
    ]
    if draft:
        cmd.append("--draft")
    
    result = subprocess.run(cmd, capture_output=True, text=True)
    if result.returncode == 0:
        print(f"✅ PR created: {result.stdout.strip()}")
    else:
        print(f"❌ Error: {result.stderr}")
    return result.returncode

def main():
    parser = argparse.ArgumentParser(description="Create GitHub PR from template")
    parser.add_argument("type", choices=TEMPLATES.keys(), help="PR type")
    parser.add_argument("--title", help="PR title")
    parser.add_argument("--head", required=True, help="Source branch")
    parser.add_argument("--base", help="Target branch (overrides template default)")
    parser.add_argument("--draft", action="store_true", help="Create as draft PR")
    
    args = parser.parse_args()
    template = load_template(args.type)
    create_pr(template, args.title, args.head, args.base, args.draft)

if __name__ == "__main__":
    main()
