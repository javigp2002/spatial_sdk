package com.meta.pixelandtexel.scanner.android.datasource.repository

import com.meta.pixelandtexel.scanner.android.datasource.SmartHomeApi
import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import javax.inject.Inject

class SmartHomeRepositoryImpl @Inject constructor(
    private val api: SmartHomeApi
) : SmartHomeRepository {

    override suspend fun getConnection(): Boolean {
        return api.getConnection().connected
    }
}