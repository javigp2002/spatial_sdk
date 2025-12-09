package com.meta.pixelandtexel.scanner.android.datasource


import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL_GET = "https://api.example.com/"

fun provideHttpClient(): OkHttpClient {
    return OkHttpClient
        .Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()
}





fun provideRetrofit(
    okHttpClient: OkHttpClient,
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL_GET)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideService(retrofit: Retrofit): SmartHomeApi =
    retrofit.create(SmartHomeApi::class.java)


val networkModule = module {
    single { provideHttpClient() }
    single { provideRetrofit(get()) }
    single { provideService(get()) }
}