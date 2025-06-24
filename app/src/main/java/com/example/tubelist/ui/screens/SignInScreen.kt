package com.example.tubelist.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tubelist.R
import com.example.tubelist.ui.components.LoadingComponent
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    authViewModel: SignInViewModel = koinViewModel(),
    onSignIn: () -> Unit
) {
    val context = LocalContext.current
    val state by authViewModel.uiState.collectAsStateWithLifecycle()
    val pendingIntent by authViewModel.pendingIntent.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (context is Activity) {
            authViewModel.handleAuthorizationResponse(result.data, context)
        }
    }

    LaunchedEffect(pendingIntent) {
        pendingIntent?.let {
            val intentSenderRequest = IntentSenderRequest.Builder(it.intentSender).build()
            launcher.launch(intentSenderRequest)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please sign in with your Google account to view your YouTube subscriptions.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            when (state) {
                is AuthState.Loading -> {
                    LoadingComponent()
                }
                is AuthState.Error -> {
                    ErrorSnippet((state as AuthState.Error).message)
                }
                is AuthState.Success -> {
                    onSignIn()
                }
                else -> {
                    SignInButton(onClick = {
                        authViewModel.signIn((context as? Activity)!!)
                    })
                }
            }
        }
    }
}

@Composable
internal fun SignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.google_g),
            contentDescription = "Google logo",
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Sign in with Google")
    }
}

@Composable
internal fun ErrorSnippet(text: String, modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(16.dp))
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center
    )
}