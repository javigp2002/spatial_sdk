package com.meta.pixelandtexel.scanner.android.domain.repository

interface SmartHomeRepository {
    suspend fun getConnection(): Boolean
}