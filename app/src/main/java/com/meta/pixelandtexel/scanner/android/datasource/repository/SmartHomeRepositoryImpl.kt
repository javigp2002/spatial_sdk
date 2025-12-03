package com.meta.pixelandtexel.scanner.android.datasource.repository

import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository

class SmartHomeRepositoryImpl (
) : SmartHomeRepository {

    override suspend fun getConnection(): Boolean {
        return true
    }
}