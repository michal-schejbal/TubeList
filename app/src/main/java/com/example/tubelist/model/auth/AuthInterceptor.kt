package com.example.tubelist.model.auth

import com.example.tubelist.app.logger.IAppLogger
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.java.KoinJavaComponent.inject

class AuthInterceptor(private val tokenStorage: ITokenStorage) : Interceptor {
    private val logger: IAppLogger by inject(IAppLogger::class.java)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStorage.getAccessToken()
        val request = if (token != null) {
            logger.d("Adding Authorization header to request: ${chain.request().url}")
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            logger.d("No access token available for request: ${chain.request().url}")
            chain.request()
        }
        return chain.proceed(request)
    }
}