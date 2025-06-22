package com.example.tubelist.app.tokens

interface ITokenStorage {
    fun getIdToken(): String?
    fun setIdToken(idToken: String)
    fun getAccessToken(): String?
    fun setAccessToken(accessToken: String)
    fun clear()
}