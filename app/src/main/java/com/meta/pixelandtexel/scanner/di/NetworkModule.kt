package com.meta.pixelandtexel.scanner.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.meta.pixelandtexel.scanner.android.datasource.SmartHomeApi
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import kotlin.jvm.java


private const val BASE_URL = "http://10.0.2.2:3000"

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder =
        Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BASE_URL)


    @Singleton
    @Provides
    fun provideMainAPIService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): SmartHomeApi =
        retrofit
            .client(okHttpClient)
            .build()
            .create(SmartHomeApi::class.java)
}

