package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.FloatingApixButton
import com.example.ui.screens.*
import com.example.ui.theme.RamezPsychologyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userProfile by viewModel.userProfile.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val motivationalToast by viewModel.motivationalToast.collectAsState()

            val isDarkMode = userProfile?.isDarkMode ?: false
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(motivationalToast) {
                motivationalToast?.let { msg ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(msg, withDismissAction = true)
                    }
                    viewModel.motivationalToast.value = null
                }
            }

            RamezPsychologyTheme(darkTheme = isDarkMode) {
                if (currentScreen == ScreenDestination.Onboarding) {
                    OnboardingScreen(
                        onComplete = { email, name, age, language, referral, rating ->
                            viewModel.completeOnboarding(
                                email = email,
                                name = name,
                                age = age,
                                language = language,
                                referralSource = referral,
                                ratingPercent = rating
                            )
                        }
                    )
                } else {
                    BackHandler(enabled = currentScreen != ScreenDestination.TasksAndPlans) {
                        viewModel.navigateTo(ScreenDestination.TasksAndPlans)
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == ScreenDestination.TasksAndPlans,
                                    onClick = { viewModel.navigateTo(ScreenDestination.TasksAndPlans) },
                                    icon = { Icon(Icons.Default.Checklist, contentDescription = "الخطط والمهام") },
                                    label = { Text("الخطط") },
                                    modifier = Modifier.testTag("nav_tasks")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == ScreenDestination.Feelings,
                                    onClick = { viewModel.navigateTo(ScreenDestination.Feelings) },
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = "تعبير عن المشاعر") },
                                    label = { Text("المشاعر") },
                                    modifier = Modifier.testTag("nav_feelings")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == ScreenDestination.ApixAi,
                                    onClick = { viewModel.navigateTo(ScreenDestination.ApixAi) },
                                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Apix الذكي") },
                                    label = { Text("Apix") },
                                    modifier = Modifier.testTag("nav_apix")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == ScreenDestination.Ebook,
                                    onClick = { viewModel.navigateTo(ScreenDestination.Ebook) },
                                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "كتاب د. رامز") },
                                    label = { Text("الكتاب") },
                                    modifier = Modifier.testTag("nav_ebook")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == ScreenDestination.SettingsProfile,
                                    onClick = { viewModel.navigateTo(ScreenDestination.SettingsProfile) },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "الإعدادات") },
                                    label = { Text("الإعدادات") },
                                    modifier = Modifier.testTag("nav_settings")
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentScreen) {
                                ScreenDestination.TasksAndPlans -> PlansTasksScreen(viewModel = viewModel)
                                ScreenDestination.Feelings -> FeelingsScreen(viewModel = viewModel)
                                ScreenDestination.ApixAi -> ApixScreen(viewModel = viewModel)
                                ScreenDestination.Ebook -> EbookScreen()
                                ScreenDestination.SettingsProfile -> ProfileSettingsScreen(viewModel = viewModel)
                                ScreenDestination.Onboarding -> {}
                            }

                            // Apix floating circular button visible on all other sections
                            if (currentScreen != ScreenDestination.ApixAi) {
                                FloatingApixButton(
                                    onNavigateToApix = {
                                        viewModel.navigateTo(ScreenDestination.ApixAi)
                                    },
                                    onExecuteVoiceCommand = { cmd ->
                                        viewModel.handleApixActionCommand(cmd)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(bottom = 12.dp, end = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
