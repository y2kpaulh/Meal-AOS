package com.echadworks.meal.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object ApiService {
    private const val TAG = "ApiService"
    // Timeout for the network requests
    private const val REQUEST_TIMEOUT = 3L

    private var okHttpClient: OkHttpClient? = null

    fun getOkHttpClient(): OkHttpClient {
        return if (okHttpClient == null) {
            val loggingInterceptor =
                HttpLoggingInterceptor { message -> Log.d(TAG, message) }
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            loggingInterceptor.redactHeader("x-amz-cf-id")

            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
//                .addInterceptor(HeaderInterceptor())
//                .authenticator(TokenAuthenticator())
//                .addInterceptor(TokenInterceptor())
                .addInterceptor(loggingInterceptor)
                .build()
            ApiService.okHttpClient = okHttpClient
            okHttpClient
        } else {
            okHttpClient!!
        }
    }
}