package com.example.tubelist.model.user

import android.content.Context
import android.util.Base64
import com.example.tubelist.app.tokens.ITokenStorage
import com.google.gson.Gson
import org.koin.java.KoinJavaComponent.inject

data class User(
    val name: String,
    val email: String,
    val avatarUrl: String? = null
)

private data class GoogleIdTokenPayload(
    val name: String?,
    val email: String?,
    val picture: String?
)

class UserRepository(private val context: Context) : IUserRepository {
    private val tokenStorage: ITokenStorage by inject(ITokenStorage::class.java)

    override fun getUser() : User? {
        val user = tokenStorage.getIdToken()?.let {
            parseIdToken(it)
        }

        return user
    }

    private fun parseIdToken(idToken: String): User {
        val parts = idToken.split(".")
        if (parts.size != 3) {
            return User("", "", "")
        }

        val json = String(
            Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        )

        val payload = Gson().fromJson(json, GoogleIdTokenPayload::class.java)

        return User(
            name = payload.name.orEmpty(),
            email = payload.email.orEmpty(),
            avatarUrl = payload.picture.orEmpty()
        )
    }
}