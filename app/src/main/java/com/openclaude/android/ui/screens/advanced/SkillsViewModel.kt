package com.openclaude.android.ui.screens.advanced

import androidx.lifecycle.ViewModel
import com.openclaude.android.data.model.Skill
import com.openclaude.android.domain.usecase.SkillsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SkillsUiState(val skills: List<Skill> = emptyList(), val selectedCategory: String = "all", val selectedSkill: Skill? = null)

@HiltViewModel
class SkillsViewModel @Inject constructor(private val skillsUseCase: SkillsUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(SkillsUiState())
    val uiState: StateFlow<SkillsUiState> = _uiState.asStateFlow()

    init { _uiState.update { it.copy(skills = skillsUseCase.getBuiltinSkills()) } }

    fun filterByCategory(category: String) {
        val all = skillsUseCase.getBuiltinSkills()
        _uiState.update { it.copy(selectedCategory = category, skills = if (category == "all") all else all.filter { s -> s.category == category }) }
    }

    fun selectSkill(skill: Skill) { _uiState.update { it.copy(selectedSkill = skill) } }
    fun toggleSkill(skill: Skill) { _uiState.update { it.copy(skills = it.skills.map { s -> if (s.id == skill.id) s.copy(isEnabled = !s.isEnabled) else s }) } }
}
