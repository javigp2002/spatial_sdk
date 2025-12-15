// Kotlin
package com.meta.pixelandtexel.scanner.android.datasource

import com.meta.pixelandtexel.scanner.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface TokenProvider {
    fun bearerToken(): String
}

class StaticTokenProvider(
    private val token: String
) : TokenProvider {
    override fun bearerToken(): String = "Bearer $token"
}

const val BASE_URL_GET = BuildConfig.HTTP_API

private fun authInterceptor(tokenProvider: TokenProvider) = Interceptor { chain ->
    val request = chain.request()
        .newBuilder()
        .addHeader("Authorization", tokenProvider.bearerToken())
        .addHeader("Content-Type", "application/json")
        .build()
    chain.proceed(request)
}

fun provideHttpClient(tokenProvider: TokenProvider): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(authInterceptor(tokenProvider))
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
    // Reemplaza el token por el tuyo
    single<TokenProvider> { StaticTokenProvider(token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJiOWE1NWRiZmQ1ZTY0ZmQxYjQ2ZDVmNTA3NDA2MTAzNSIsImlhdCI6MTc2NTM3MTgzMSwiZXhwIjoyMDgwNzMxODMxfQ.C_0p6QAEsMSpi-5NXfP8zEk48jc65BE8y0gXdkU_vPs") }
    single { provideHttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideService(get()) }
}