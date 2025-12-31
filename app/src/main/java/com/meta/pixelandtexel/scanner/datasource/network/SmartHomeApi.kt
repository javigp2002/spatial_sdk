package com.meta.pixelandtexel.scanner.datasource.network

import com.meta.pixelandtexel.scanner.android.datasource.dto.ConnectionDto
import com.meta.pixelandtexel.scanner.android.datasource.dto.DeviceListResponseDto
import com.meta.pixelandtexel.scanner.android.datasource.dto.EntityIdDto
import com.meta.pixelandtexel.scanner.android.datasource.dto.ThingsResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SmartHomeApi {
    @GET("api")
    suspend fun getConnection(): ConnectionDto

    @POST("services/switch/turn_on")
    suspend fun turnOnSwitch(
        @Body body: EntityIdDto
    ): Response<Unit>

    @POST("services/switch/turn_off")
    suspend fun turnOffSwitch(
        @Body body: EntityIdDto
    ): Response<Unit>


    @POST("template")
    suspend fun postTemplate(
        @Body body: Map<String, String>
    ): Response<DeviceListResponseDto>

    @GET("states/{entity_id}")
    suspend fun getEntityState(
        @Path("entity_id") entityId: String
    ): Response<ThingsResponseDto>
}