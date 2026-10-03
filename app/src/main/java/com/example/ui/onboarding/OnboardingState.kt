package com.example.ui.onboarding

data class OnboardingState(
    val currentStep: Int = 0, // 0 = Welcome hero, 1..10 = Questions, 11 = Synthesis / Building routine
    val focusArea: String = "",
    val consistency: String = "",
    val hurdle: String = "",
    val habitCountLoad: String = "",
    val primeTime: String = "",
    val selectedHabits: Set<String> = emptySet(),
    val motivationStyle: String = "",
    val timeCommitment: String = "",
    val briefingTime: String = "",
    val userName: String = "",
    val headline: String = "",
    val selectedOccupations: Set<String> = emptySet(),
    val customOccupation: String = "",
    val pledgeConfirmed: Boolean = false,
    val isAnalyzing: Boolean = false,
    val analysisProgress: Float = 0f,
    val isCompleted: Boolean = false
)
