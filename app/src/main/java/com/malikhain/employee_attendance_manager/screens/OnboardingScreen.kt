package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.malikhain.employee_attendance_manager.data.PreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController, preferencesManager: PreferencesManager) {
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to Employee Attendance Manager",
            description = "Efficiently manage your team's attendance with our comprehensive solution",
            icon = Icons.Default.Person
        ),
        OnboardingPage(
            title = "Easy Attendance Tracking",
            description = "Mark attendance for employees with just a few taps. Support for Present, Absent, and Leave status",
            icon = Icons.Default.Info
        ),
        OnboardingPage(
            title = "Data Export & Backup",
            description = "Export attendance data to CSV and create backups to keep your data safe",
            icon = Icons.Default.Settings
        ),
        OnboardingPage(
            title = "Ready to Get Started?",
            description = "Create your admin account and start managing your team's attendance today",
            icon = Icons.Default.Check
        )
    )
    
    val pagerState = rememberPagerState(pageCount = { pages.size })
    var currentPage by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(pagerState.currentPage) {
        currentPage = pagerState.currentPage
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Skip button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { 
                    coroutineScope.launch {
                        preferencesManager.setOnboardingCompleted(true)
                    }
                    navController.navigate("register")
                }
            ) {
                Text("Skip")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(pages[page])
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Page indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = MaterialTheme.shapes.small,
                        color = if (currentPage == index) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    ) {}
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentPage > 0) {
                TextButton(
                    onClick = {
                        // Navigate to previous page
                    }
                ) {
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }
            
            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        // Navigate to next page
                    } else {
                        coroutineScope.launch {
                            preferencesManager.setOnboardingCompleted(true)
                        }
                        navController.navigate("register")
                    }
                }
            ) {
                Text(if (currentPage < pages.size - 1) "Next" else "Get Started")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    if (currentPage < pages.size - 1) Icons.Default.ArrowForward else Icons.Default.Check,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) 