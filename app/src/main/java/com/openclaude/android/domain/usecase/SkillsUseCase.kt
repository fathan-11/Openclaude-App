package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.Skill
import javax.inject.Inject

class SkillsUseCase @Inject constructor() {
    fun getBuiltinSkills(): List<Skill> = listOf(
        Skill("code-review", "Code Review", "Review code for quality and issues", "development"),
        Skill("refactor", "Refactor", "Refactor code for better structure", "development"),
        Skill("explain", "Explain Code", "Explain how code works", "learning"),
        Skill("test-gen", "Test Generator", "Generate unit tests", "testing"),
        Skill("doc-gen", "Documentation", "Generate documentation", "docs"),
        Skill("commit-msg", "Commit Message", "Generate commit messages", "git"),
        Skill("branch-name", "Branch Name", "Suggest branch names", "git"),
        Skill("debug", "Debug", "Help debug issues", "development")
    )
}
