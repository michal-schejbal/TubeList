package com.example.tubelist.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tubelist.model.user.User
import com.example.tubelist.ui.components.LoadingComponent
import com.example.tubelist.ui.theme.TubeListTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, viewModel: ProfileViewModel = koinViewModel(), onLogout: () -> Unit) {
    val user by viewModel.user.collectAsState()
    val context = LocalActivity.current

    ProfileScreenContent(
        user = user,
        onLogout = {
            context?.let { activity -> viewModel.logout(activity) }
            onLogout()
        },
        modifier = modifier
    )
}

@Composable
fun ProfileScreenContent(
    user: User?,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        user?.let {
            ProfileImage(it.avatarUrl)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = it.name, style = MaterialTheme.typography.titleLarge)
            Text(text = it.email, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onLogout) {
                Text("Logout")
            }
        } ?: LoadingComponent()
    }
}

@Composable
internal fun ProfileImage(avatarUrl: String?) {
    if (avatarUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Profile image",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Default profile icon",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.4f))
                .padding(6.dp),
            tint = Color.DarkGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileContentPreview() {
    TubeListTheme {
        ProfileScreenContent(
            user = User(
                name = "User",
                email = "user@example.com",
            ),
            onLogout = {}
        )
    }
}
