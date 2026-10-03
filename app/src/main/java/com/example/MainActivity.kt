package com.example

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.HabitRepository
import com.example.notifications.ForgeNotificationHelper
import com.example.ui.dashboard.HabitDashboardViewModel
import com.example.ui.dashboard.MainDashboardScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.onboarding.OnboardingViewModel
import com.example.ui.theme.ForgeHabitsTheme
import com.example.ui.theme.ObsidianBlack

class MainActivity : ComponentActivity() {

    private var dashboardViewModelRef: HabitDashboardViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ForgeNotificationHelper.initNotificationChannels(applicationContext)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = HabitRepository(
            database.habitDao(),
            database.userProfileDao(),
            database.taskDao(),
            database.bossChallengeDao()
        )

        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                        OnboardingViewModel(repository) as T
                    }
                    modelClass.isAssignableFrom(HabitDashboardViewModel::class.java) -> {
                        val vm = HabitDashboardViewModel(repository)
                        dashboardViewModelRef = vm
                        vm as T
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

        setContent {
            ForgeHabitsTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianBlack
                ) {
                    val onboardingViewModel: OnboardingViewModel = viewModel(factory = factory)
                    val dashboardViewModel: HabitDashboardViewModel = viewModel(factory = factory)

                    // Request notification permission on Android 13+
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { _ -> }

                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }

                        // Handle initial intent target tab from push notification
                        val targetTab = intent?.getIntExtra("navigate_to_tab", -1) ?: -1
                        if (targetTab >= 0) {
                            dashboardViewModel.selectTab(targetTab)
                        }
                    }

                    val prefs = remember {
                        this@MainActivity.getSharedPreferences("forge_habits_prefs", Context.MODE_PRIVATE)
                    }
                    val userProfile by dashboardViewModel.userProfile.collectAsState()
                    val isProfileLoaded by dashboardViewModel.isProfileLoaded.collectAsState()

                    // Keep synchronous cache updated
                    LaunchedEffect(userProfile) {
                        userProfile?.let {
                            prefs.edit().putBoolean("onboarding_completed", it.isOnboardingCompleted).apply()
                        }
                    }

                    // Determine completed state: use Room if loaded, otherwise fall back to instant sync cache
                    val hasCachedCompletion = remember {
                        prefs.getBoolean("onboarding_completed", false)
                    }
                    val isCompleted = if (userProfile != null) {
                        userProfile?.isOnboardingCompleted == true
                    } else {
                        hasCachedCompletion
                    }

                    // If profile is not yet loaded and no cache exists, keep obsidian black background to avoid flash
                    if (!isProfileLoaded && !hasCachedCompletion) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ObsidianBlack)
                        )
                    } else {
                        AnimatedContent(
                            targetState = isCompleted,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "app_root_transition"
                        ) { completed ->
                            if (completed) {
                                MainDashboardScreen(
                                    viewModel = dashboardViewModel,
                                    onRetakeOnboarding = {
                                        prefs.edit().putBoolean("onboarding_completed", false).apply()
                                        dashboardViewModel.retakeOnboarding()
                                    }
                                )
                            } else {
                                OnboardingScreen(
                                    viewModel = onboardingViewModel,
                                    onFinished = {
                                        prefs.edit().putBoolean("onboarding_completed", true).apply()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val targetTab = intent.getIntExtra("navigate_to_tab", -1)
        if (targetTab >= 0) {
            dashboardViewModelRef?.selectTab(targetTab)
        }
    }
}
