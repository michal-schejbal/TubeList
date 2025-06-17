package com.example.tubelist.app

object TokenStorage : ITokenStorage {
    private val ID_TOKEN = "id_token"
    private val ACCESS_TOKEN = "access_token"

    // TODO store in encrypted preferences instead of memory
//    private val settings: ISettings by inject(ISettings::class.java)
    private var idToken: String? = null
    private var accessToken: String? = null

    override fun getIdToken(): String? {
        return idToken
    }

    override fun setIdToken(idToken: String) {
        this.idToken = idToken
    }

    override fun getAccessToken(): String? {
        return accessToken
    }

    override fun setAccessToken(accessToken: String) {
        this.accessToken = accessToken
    }

    override fun clear() {
        idToken = null
        accessToken = null
    }
}