package com.akvelon.grimmuzzle.di

import com.akvelon.grimmuzzle.GrimmuzzleApplication
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.api.GrimmuzzleAPI
import com.akvelon.grimmuzzle.ui.MainActivity
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class GrimmuzzleAPIModule {

    private val baseUrl =
        GrimmuzzleApplication.instance.resources.getString(R.string.base_grimmuzzle_api_url)
    private val cacheSize = (32 * 1024 * 1024).toLong()

    @Singleton
    @Provides
    fun getGrimmuzzleAPI(retrofit: Retrofit): GrimmuzzleAPI {
        return retrofit.create(GrimmuzzleAPI::class.java)
    }

    @Provides
    fun getOkHttpClient(): OkHttpClient {
        val cache = Cache(MainActivity.instance.cacheDir, cacheSize)
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            .build()
    }

    @Provides
    @Singleton
    fun getRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}