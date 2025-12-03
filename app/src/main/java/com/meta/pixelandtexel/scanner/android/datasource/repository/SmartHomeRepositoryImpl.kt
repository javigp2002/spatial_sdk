package com.meta.pixelandtexel.scanner.android.datasource.repository

import com.meta.pixelandtexel.scanner.android.datasource.SmartHomeApi
import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import kotlinx.serialization.InternalSerializationApi

class SmartHomeRepositoryImpl (
    private val api: SmartHomeApi
) : SmartHomeRepository {

    @OptIn(InternalSerializationApi::class)
    override suspend fun getConnection(): Boolean {
        try{
            val response = api.getConnection()
            return response.connected
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}