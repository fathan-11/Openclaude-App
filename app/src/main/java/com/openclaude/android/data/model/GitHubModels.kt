package com.openclaude.android.data.model

enum class GitHubRepoVisibility { PUBLIC, PRIVATE }
enum class PrState { OPEN, CLOSED, MERGED }
enum class IssueState { OPEN, CLOSED }

data class GitHubRepo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String = "",
    val language: String = "",
    val stars: Int = 0,
    val forks: Int = 0,
    val visibility: GitHubRepoVisibility = GitHubRepoVisibility.PUBLIC,
    val defaultBranch: String = "main",
    val updatedAt: String = ""
)

data class GitHubPR(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String = "",
    val state: PrState = PrState.OPEN,
    val author: String = "",
    val headBranch: String = "",
    val baseBranch: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val comments: Int = 0,
    val additions: Int = 0,
    val deletions: Int = 0,
    val changedFiles: Int = 0,
    val isDraft: Boolean = false
)

data class GitHubIssue(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String = "",
    val state: IssueState = IssueState.OPEN,
    val author: String = "",
    val labels: List<String> = emptyList(),
    val assignees: List<String> = emptyList(),
    val milestone: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val comments: Int = 0
)

data class GitHubCommit(
    val sha: String,
    val message: String,
    val author: String,
    val date: String,
    val additions: Int = 0,
    val deletions: Int = 0
)

data class GitHubReview(
    val id: Long,
    val prNumber: Int,
    val reviewer: String,
    val state: String, // APPROVED, CHANGES_REQUESTED, COMMENTED
    val body: String = "",
    val submittedAt: String = ""
)
