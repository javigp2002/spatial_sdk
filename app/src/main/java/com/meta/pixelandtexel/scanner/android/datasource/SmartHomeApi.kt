package com.meta.pixelandtexel.scanner.android.datasource

import retrofit2.http.GET
import com.meta.pixelandtexel.scanner.android.datasource.dto.ConnectionDto

interface SmartHomeApi {
    @GET("api/connection")
    suspend fun getConnection(): ConnectionDto
}