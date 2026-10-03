package com.example.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HabitRepository
import com.example.data.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun nextStep() {
        if (_state.value.currentStep == 11) {
            startAnalysisAndThenIdentity()
        } else {
            _state.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun previousStep() {
        if (_state.value.currentStep > 0 && !_state.value.isAnalyzing) {
            _state.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    fun setFocusArea(area: String) {
        _state.update { it.copy(focusArea = area) }
    }

    fun setConsistency(consistency: String) {
        _state.update { it.copy(consistency = consistency) }
    }

    fun setHurdle(hurdle: String) {
        _state.update { it.copy(hurdle = hurdle) }
    }

    fun setHabitCountLoad(load: String) {
        _state.update { it.copy(habitCountLoad = load) }
    }

    fun setPrimeTime(time: String) {
        _state.update { it.copy(primeTime = time) }
    }

    fun toggleStarterHabit(habitName: String) {
        _state.update { current ->
            val set = current.selectedHabits.toMutableSet()
            if (set.contains(habitName)) {
                if (set.size > 1) { // keep at least 1 selected
                    set.remove(habitName)
                }
            } else {
                set.add(habitName)
            }
            current.copy(selectedHabits = set)
        }
    }

    fun setMotivationStyle(style: String) {
        _state.update { it.copy(motivationStyle = style) }
    }

    fun setTimeCommitment(commitment: String) {
        _state.update { it.copy(timeCommitment = commitment) }
    }

    fun setBriefingTime(time: String) {
        _state.update { it.copy(briefingTime = time) }
    }

    fun setUserName(name: String) {
        _state.update { it.copy(userName = name) }
    }

    fun setHeadline(headline: String) {
        _state.update { it.copy(headline = headline) }
    }

    fun toggleOccupation(occupation: String) {
        _state.update { current ->
            val set = current.selectedOccupations.toMutableSet()
            if (set.contains(occupation)) {
                set.remove(occupation)
            } else {
                set.add(occupation)
            }
            current.copy(selectedOccupations = set)
        }
    }

    fun setCustomOccupation(custom: String) {
        _state.update { it.copy(customOccupation = custom) }
    }

    fun addCustomOccupation() {
        val custom = _state.value.customOccupation.trim()
        if (custom.isNotBlank()) {
            _state.update { current ->
                val set = current.selectedOccupations.toMutableSet()
                set.add(custom)
                current.copy(selectedOccupations = set, customOccupation = "")
            }
        }
    }

    fun setPledgeConfirmed(confirmed: Boolean) {
        _state.update { it.copy(pledgeConfirmed = confirmed) }
        if (confirmed) {
            startAnalysisAndThenIdentity()
        }
    }

    private fun startAnalysisAndThenIdentity() {
        _state.update { it.copy(currentStep = 11, isAnalyzing = true, analysisProgress = 0f) }
        viewModelScope.launch {
            // Smooth 3.0 second circulating animation (30 frames x 100ms = 3000ms)
            for (i in 1..30) {
                delay(100)
                _state.update { it.copy(analysisProgress = i / 30f) }
            }
            delay(200)
            // After fingerprint animation and info gathering animation end, show the identity form!
            _state.update { it.copy(currentStep = 12, isAnalyzing = false) }
        }
    }

    fun finalizeProfileAndComplete() {
        viewModelScope.launch {
            val finalOccupations = (_state.value.selectedOccupations +
                listOfNotNull(_state.value.customOccupation.trim().ifBlank { null }))
                .filter { it.isNotBlank() }

            val occupationsString = if (finalOccupations.isNotEmpty()) {
                finalOccupations.joinToString(", ")
            } else {
                "Computer Science, Client Freelancer, Visual Creator"
            }

            val fallbackHeadline = if (finalOccupations.isNotEmpty()) {
                finalOccupations.take(2).joinToString(" & ")
            } else {
                "CS Student & Visual Creator"
            }

            // Save to database
            val profile = UserProfile(
                userName = _state.value.userName.trim().ifBlank { "Alex" },
                headline = _state.value.headline.trim().ifBlank { fallbackHeadline },
                occupations = occupationsString,
                focusArea = _state.value.focusArea.ifBlank { "Physical Vitality" },
                currentConsistency = _state.value.consistency.ifBlank { "Starting fresh" },
                frictionHurdle = _state.value.hurdle.ifBlank { "Time constraints" },
                targetHabitLoad = _state.value.habitCountLoad.ifBlank { "3-4 Balanced routine" },
                primeZone = _state.value.primeTime.ifBlank { "Early Morning" },
                starterHabits = _state.value.selectedHabits.joinToString(","),
                motivationStyle = _state.value.motivationStyle.ifBlank { "Streak Counter" },
                timeCommitment = _state.value.timeCommitment.ifBlank { "15 - 30 minutes" },
                briefingTime = _state.value.briefingTime.ifBlank { "07:30 AM" },
                pledgeAccepted = true,
                isOnboardingCompleted = true
            )

            repository.completeOnboarding(
                profile,
                _state.value.selectedHabits.ifEmpty { setOf("Drink 2L Water", "Read 15 Pages") }.toList()
            )
            delay(200)
            _state.update { it.copy(isCompleted = true) }
        }
    }
}
