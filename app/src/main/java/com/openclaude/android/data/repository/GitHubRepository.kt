package com.openclaude.android.data.repository

import com.openclaude.android.data.model.*
import com.openclaude.android.data.remote.GitHubApiService
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepository @Inject constructor(
    private val gitHubApiService: GitHubApiService
) {
    private val _repos = MutableStateFlow<List<GitHubRepo>>(emptyList())
    val repos: StateFlow<List<GitHubRepo>> = _repos.asStateFlow()

    suspend fun loadRepos(): Result<List<GitHubRepo>> = try {
        val repos = gitHubApiService.getRepos()
        _repos.value = repos
        Result.success(repos)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getRepo(owner: String, repo: String): Result<GitHubRepo> = try {
        Result.success(gitHubApiService.getRepo(owner, repo))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getPRs(owner: String, repo: String, state: String = "open"): Result<List<GitHubPR>> = try {
        Result.success(gitHubApiService.getPRs(owner, repo, state))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getIssues(owner: String, repo: String, state: String = "open"): Result<List<GitHubIssue>> = try {
        Result.success(gitHubApiService.getIssues(owner, repo, state))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getCommits(owner: String, repo: String): Result<List<GitHubCommit>> = try {
        Result.success(gitHubApiService.getCommits(owner, repo))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getReviews(owner: String, repo: String, prNumber: Int): Result<List<GitHubReview>> = try {
        Result.success(gitHubApiService.getReviews(owner, repo, prNumber))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun createIssue(owner: String, repo: String, title: String, body: String): Result<GitHubIssue> = try {
        Result.success(gitHubApiService.createIssue(owner, repo, mapOf("title" to title, "body" to body)))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun createPR(owner: String, repo: String, title: String, body: String, head: String, base: String): Result<GitHubPR> = try {
        Result.success(gitHubApiService.createPR(owner, repo, mapOf("title" to title, "body" to body, "head" to head, "base" to base)))
    } catch (e: Exception) { Result.failure(e) }
}
