package com.example.tubelist.model.auth

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.example.tubelist.app.Config
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.api.services.youtube.YouTubeScopes
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.java.KoinJavaComponent.inject
import java.security.MessageDigest
import java.util.UUID
import kotlin.coroutines.resume

sealed class AuthResult {
    data class Success(val token: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
    data class ResolutionRequired(val intent: PendingIntent) : AuthResult()
}

/**
 * https://developer.android.com/identity/sign-in
 * Manages Google account authentication and authorization
 */
class AuthManager : IAuthManager {
    private val tokenStorage: ITokenStorage by inject(ITokenStorage::class.java)

    override suspend fun signIn(context: Activity): AuthResult {
        val result = requestIdToken(context)

        return if (result is AuthResult.Success) {
            requestAccessToken(context, listOf(
                Scope(YouTubeScopes.YOUTUBE_READONLY)
            ))
        } else {
            result
        }
    }

    private suspend fun requestIdToken(context: Activity): AuthResult {
        val credentialManager = CredentialManager.create(context)

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(true)
                .setServerClientId(Config.CLIENT_ID)
                .setNonce(generateNonce())
                .build())
            .build()

        return try {
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context,
            )

            val credential = result.credential
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val data = GoogleIdTokenCredential.createFrom(credential.data)
                val token = data.idToken
                tokenStorage.setIdToken(token)
                AuthResult.Success(token)
            } else {
                AuthResult.Error("Unrecognized credential type.")
            }
        } catch (e: GoogleIdTokenParsingException) {
            AuthResult.Error("Received an invalid google id token response")
        } catch (e: GetCredentialException) {
            AuthResult.Error(e.message ?: "An unknown error occurred.")
        }
    }

    private suspend fun requestAccessToken(context: Activity, scopes: List<Scope>): AuthResult {
        val authorizationRequest = AuthorizationRequest.builder()
            .setRequestedScopes(scopes)
            .build()

        val client = Identity.getAuthorizationClient(context)

        return suspendCancellableCoroutine { continuation ->
            client.authorize(authorizationRequest)
                .addOnSuccessListener { result ->
                    if (result.hasResolution() && result.pendingIntent != null) {
                        continuation.resume(AuthResult.ResolutionRequired(result.pendingIntent!!))
                    } else if (result.accessToken != null) {
                        val token = result.accessToken!!
                        tokenStorage.setAccessToken(token)
                        continuation.resume(AuthResult.Success(token))
                    } else {
                        continuation.resume(AuthResult.Error("Authorization succeeded but token was null."))
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resume(AuthResult.Error(e.message ?: "Authorization failed."))
                }
        }
    }

    override fun handleAuthorizationResult(intent: Intent?, context: Activity): AuthResult {
        return try {
            val result = Identity.getAuthorizationClient(context)
                .getAuthorizationResultFromIntent(intent)

            if (result.accessToken != null) {
                val token = result.accessToken!!
                tokenStorage.setAccessToken(token)
                AuthResult.Success(token)
            } else {
                AuthResult.Error("Authorization was cancelled or failed.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to parse authorization result.")
        }
    }

    @Deprecated("GoogleSignInOptions should no longer be used since separation of authentication and authorization is recommended.")
    private suspend fun signInGoogleSignIn(context: Activity): AuthResult {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(YouTubeScopes.YOUTUBE_READONLY))
            .requestIdToken(Config.CLIENT_ID)
            .build()

        val signInClient = GoogleSignIn.getClient(context, gso)
        val result = suspendCancellableCoroutine<AuthResult> { continuation ->
            signInClient.silentSignIn()
                .addOnSuccessListener { account ->
                    try {
                        val token = GoogleAuthUtil.getToken(
                            context,
                            account.account!!,
                            "oauth2:${YouTubeScopes.YOUTUBE_READONLY}"
                        )
                        tokenStorage.setIdToken(token)
                        continuation.resume(AuthResult.Success(token))
                    } catch (e: Exception) {
                        continuation.resume(AuthResult.Error("Token retrieval failed: ${e.localizedMessage}"))
                    }
                }
                .addOnFailureListener {
                    continuation.resume(AuthResult.Error("Silent sign-in failed. Prompting user."))
//                    // Fallback: prompt user interactively (activity should handle this)
//                    activity.startActivityForResult(signInClient.signInIntent, RC_SIGN_IN)
                }
        } ?: AuthResult.Error("Silent sign-in failed")
        return result
    }

    override fun signOut(context: Activity) {
        tokenStorage.clear()
    }

    @Deprecated("GoogleSignInOptions should no longer be used.")
    private fun signOutGoogleSignIn(context: Activity) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(Config.CLIENT_ID)
            .build()

        GoogleSignIn.getClient(context, gso).signOut()
            .addOnCompleteListener {
            }

        GoogleSignIn.getClient(context, gso).revokeAccess()
    }

    /**
     * Generate nonce random string used to prevent replay attacks
     */
    private fun generateNonce(length: Int = 32): String {
        val random = UUID.randomUUID().toString()
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(random.toByteArray())
        return hash.toHexString().substring(0, length)
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}