package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.*
import com.openclaude.android.data.repository.GitHubRepository
import javax.inject.Inject

class LoadReposUseCase @Inject constructor(private val repo: GitHubRepository) {
    suspend operator fun invoke() = repo.loadRepos()
}

class GetRepoDetailsUseCase @Inject constructor(private val repo: GitHubRepository) {
    suspend operator fun invoke(owner: String, name: String) = repo.getRepo(owner, name)
}

class GetPullRequestsUseCase @Inject constructor(private val repo: GitHubRepository) {
    suspend operator fun invoke(owner: String, name: String, state: String = "open") = repo.getPRs(owner, name, state)
}

class GetIssuesUseCase @Inject constructor(private val repo: GitHubRepository) {
    suspend operator fun invoke(owner: String, name: String, state: String = "open") = repo.getIssues(owner, name, state)
}

class GetCommitsUseCase @Inject constructor(private val repo: GitHubRepository) {
    suspend operator fun invoke(owner: String, name: String) = repo.getCommits(owner, name)
}

class CreateIssueUseCase @Inject constructor(private val repo: GitHubRepository) {
    suspend operator fun invoke(owner: String, name: String, title: String, body: String) = repo.createIssue(owner, name, title, body)
}

class CreatePullRequestUseCase @Inject constructor(private val repo: GitHubRepository) {
    suspend operator fun invoke(owner: String, name: String, title: String, body: String, head: String, base: String) = repo.createPR(owner, name, title, body, head, base)
}
