package com.example.tubelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tubelist.ui.screens.ChannelScreen
import com.example.tubelist.ui.screens.FeedScreen
import com.example.tubelist.ui.screens.ProfileScreen
import com.example.tubelist.ui.screens.ProfileViewModel
import com.example.tubelist.ui.screens.SignInScreen
import com.example.tubelist.ui.theme.TubeListTheme
import org.koin.java.KoinJavaComponent.inject

enum class Screens(val route: String, val title: String) {
    SignIn("sign_in", ""),
    Profile("profile", "Profile"),
    Feed("feed", "Your Subscriptions"),
    Channel("channel/{channelId}", "Channel");

    companion object {
        fun fromRoute(route: String?): Screens? = when {
            route == null -> null
            route.startsWith("channel/") -> Channel
            else -> entries.find { it.route == route }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TubeListTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val currentScreen = Screens.fromRoute(currentRoute)

                Scaffold(
                    topBar = {
                        if (currentRoute != Screens.SignIn.route) {
                            AppTopBar(
                                title = currentScreen?.title.orEmpty(),
                                navController = navController
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AppScreen(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    AppNavHost(navController, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, navController: NavHostController) {
    val profileViewModel: ProfileViewModel by inject(ProfileViewModel::class.java)
    val user by profileViewModel.user.collectAsState()

    TopAppBar(
        title = { Text(title) },
        actions = {
            IconButton(onClick = {
                navController.navigate(Screens.Profile.route)
            }) {
                user?.let {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user?.avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile image",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                } ?: Icon(Icons.Default.AccountCircle, contentDescription = "Account")
            }
        }
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Screens.SignIn.route) {
        composable(Screens.SignIn.route) {
            SignInScreen(modifier = modifier, onSignIn = {
                navController.navigate(Screens.Feed.route) {
                    popUpTo(Screens.SignIn.route) { inclusive = true }
                }
            })
        }
        composable(Screens.Feed.route) {
            FeedScreen(modifier = modifier, onChannelClick = { channelId ->
                navController.navigate("channel/$channelId")
            })
        }
        composable(Screens.Profile.route) {
            ProfileScreen(modifier = modifier, onLogout = {
                navController.navigate(Screens.SignIn.route) {
                    popUpTo(0)
                }
            })
        }
        composable(Screens.Channel.route) { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
            ChannelScreen(modifier = modifier, channelId = channelId)
        }
    }
}